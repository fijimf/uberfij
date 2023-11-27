package com.fijimf.deepfij.scraping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fijimf.deepfij.db.model.schedule.Game;
import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.schedule.Team;
import com.fijimf.deepfij.db.model.scrape.EspnScoreboardScrape;
import com.fijimf.deepfij.db.model.scrape.EspnSeasonScrape;
import com.fijimf.deepfij.db.model.scrape.EspnStandingsScrape;
import com.fijimf.deepfij.db.repo.schedule.GameRepo;
import com.fijimf.deepfij.db.repo.schedule.SeasonRepo;
import com.fijimf.deepfij.db.repo.schedule.TeamRepo;
import com.fijimf.deepfij.db.repo.scrape.EspnScoreboardScrapeRepo;
import com.fijimf.deepfij.db.repo.scrape.EspnSeasonScrapeRepo;
import com.fijimf.deepfij.services.Mailer;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SeasonManager {
    private final Logger logger = LoggerFactory.getLogger(SeasonManager.class);
    private final SeasonRepo repo;
    private final GameRepo gameRepo;
    private final TeamRepo teamRepo;

    private final EspnSeasonScrapeRepo seasonScrapeRepo;
    private final EspnScoreboardScrapeRepo scoreboardScrapeRepo;
    private final StandingsScrapeManager standingsMgr;
    private final ScoreboardScrapeManager scoreboardMgr;

    private final ConcurrentMap<Long, ExecutorService> executors = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final ScheduledExecutorService threadPool;

    private final Mailer mailer;

    public SeasonManager(SeasonRepo repo, GameRepo gameRepo, TeamRepo teamRepo, EspnSeasonScrapeRepo seasonScrapeRepo,
            EspnScoreboardScrapeRepo scoreboardScrapeRepo, StandingsScrapeManager standingsMgr,
            ScoreboardScrapeManager scoreboardMgr, Mailer mailer) {
        this.repo = repo;
        this.gameRepo = gameRepo;
        this.teamRepo = teamRepo;
        this.seasonScrapeRepo = seasonScrapeRepo;
        this.scoreboardScrapeRepo = scoreboardScrapeRepo;
        this.standingsMgr = standingsMgr;
        this.scoreboardMgr = scoreboardMgr;
        this.mailer = mailer;
        this.threadPool = Executors.newScheduledThreadPool(1);
    }

    @PostConstruct
    public void init() {
        logger.info("Cleaning up scrape table on startup.");
        seasonScrapeRepo.findByCompletedAtIsNull().forEach(ss -> {
            logger.info("Abandoning unfinished season scrape " + ss.getId());
            seasonScrapeRepo.saveAndFlush(ss.complete("ABANDONED"));
        });
        seasonScrapeRepo.findByCompletedAtIsNotNullAndStatus("RUNNING").forEach(ss -> {
            logger.info("Setting scrape " + ss.getId() + " with completion at " + ss.getCompletedAt()
                    + " and status 'RUNNING' to 'COMPLETED'");
            seasonScrapeRepo.saveAndFlush(ss.complete("COMPLETE"));
        });

    }

    public List<Season> findAllSeasons() {
        return repo.findAll(Sort.by("season"));
    }

    public Season findById(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public List<EspnStandingsScrape> findStandingsScrapesBySeason(int year) {
        return standingsMgr.findStandingScrapesBySeasonYear(year);
    }

    public void createNewSeason(int year) {
        repo.saveAndFlush(new Season(0L, year));
    }

    public Season findSeasonBySeason(int season) {
        return repo.findBySeason(season).orElseThrow();
    }

    public static LocalDate defaultEndDate(int yyyy) {
        return LocalDate.of(yyyy, 4, 30);
    }

    public static LocalDate defaultStartDate(int yyyy) {
        return LocalDate.of(yyyy - 1, 11, 1);
    }

    // public Long scrapeSeason(Season season, String from, String to) {
    // LocalDate start = LocalDate.parse(from,
    // DateTimeFormatter.ofPattern("yyyyMMdd"));
    // LocalDate end = LocalDate.parse(to, DateTimeFormatter.ofPattern("yyyyMMdd"));
    // return scrapeSeason(season, start, end);
    // }

    private Long scrapeSeason(Season season, LocalDate start, LocalDate end, Long timeout) {
        Random random = new Random();
        EspnSeasonScrape seasonScrape = seasonScrapeRepo.saveAndFlush(
                new EspnSeasonScrape(0L, season.getSeason(), start, end, LocalDateTime.now(), null, "STARTING"));
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executors.put(seasonScrape.getId(), executorService);
        start.datesUntil(end).forEach(d -> executorService.submit(() -> {
            long sleepDelay = random.nextLong(500L, 2000L);
            logger.info("Loading " + d + " - delay is " + sleepDelay + " ms");
            try {
                Thread.sleep(sleepDelay);
            } catch (InterruptedException e) {
                // throw new RuntimeException(e);
            }
            scrapeDate(d, seasonScrape.getId());
            seasonScrapeRepo.updateStatusById(seasonScrape.getId(), "RUNNING");

        }));

        executorService.shutdown();

        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                boolean b = executorService.awaitTermination(timeout, TimeUnit.SECONDS);
                if (b) {
                    updateSeasonScrapeStatus(seasonScrape, "COMPLETED");
                    executors.remove(seasonScrape.getId());
                } else {
                    updateSeasonScrapeStatus(seasonScrape, "TIMED OUT");
                    executorService.shutdownNow();
                    executors.remove(seasonScrape.getId());
                }
            } catch (InterruptedException e) {
                updateSeasonScrapeStatus(seasonScrape, "ABORTED");
                executors.remove(seasonScrape.getId());
            }

        });
        return seasonScrape.getId();
    }

    public void cancelSeasonScrape(Long id) {
        ExecutorService executorService = executors.remove(id);
        if (executorService != null) {
            executorService.shutdownNow();
            seasonScrapeRepo.findById(id).ifPresent(s -> updateSeasonScrapeStatus(s, "CANCELLED"));
        }
    }

    private void updateSeasonScrapeStatus(EspnSeasonScrape seasonScrape, String status) {
        seasonScrapeRepo.findById(seasonScrape.getId()).ifPresent(s -> {
            logger.info("Setting scrape " + seasonScrape.getId() + " from " + s.getStatus() + " to " + status);
            if (s.getCompletedAt() != null) {
                logger.warn("Trying to set completion status on complete season scrape. Ignoring");
            } else {
                s.setCompletedAt(LocalDateTime.now());
                s.setStatus(status);
                seasonScrapeRepo.saveAndFlush(s);
            }
        });
    }

    private void scrapeDate(LocalDate date, long seasonScrapeId) {
        scoreboardMgr.scrapeScoreboardDate(date, seasonScrapeId);
    }

    public Long scrapeSeasonByYear(int year, String from, String to, String timeOutSec) {
        Season season = findSeasonBySeason(year);
        LocalDate start = StringUtils.isNotBlank(from) ? LocalDate.parse(from, DateTimeFormatter.ofPattern("yyyyMMdd"))
                : defaultStartDate(year);
        LocalDate end = StringUtils.isNotBlank(to) ? LocalDate.parse(to, DateTimeFormatter.ofPattern("yyyyMMdd"))
                : defaultEndDate(year);
        Long timeout = StringUtils.isNotBlank(timeOutSec) ? Long.parseLong(timeOutSec) : 3600L;
        return scrapeSeason(season, start, end, timeout);
    }

    public List<EspnSeasonScrape> findSeasonScrapesBySeason(Season season) {
        return seasonScrapeRepo.findAllBySeasonOrderByStartedAt(season.getSeason());
    }

    public List<PublishResult> publishSeasonScrape(long seasonScrapeId) {
        return seasonScrapeRepo.findById(seasonScrapeId).map(
                ess -> ess.getScoreboardScrapes()
                        .stream()
                        .map(this::publishScoreboardScrape)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList())
                .orElse(Collections.emptyList());

    }

    public void publishScoreboardScrape(long id) {
        scoreboardScrapeRepo.findById(id).ifPresent(this::publishScoreboardScrape);
    }

    public Optional<PublishResult> publishScoreboardScrape(EspnScoreboardScrape sbs) {
        LocalDate scoreboardKey = sbs.getScoreboardKey();

        Map<String, Game> gameMap = gameRepo
                .findAllByScoreboardKey(scoreboardKey)
                .stream()
                .collect(Collectors.toMap(Game::getEspnId, Function.identity()));
        Map<String, Team> teamMap = teamRepo
                .findAll()
                .stream()
                .collect(Collectors.toMap(Team::getEspnId, Function.identity()));
        long sbsId = sbs.getId();
        logger.info("For scoreboard scrape " + sbsId + "(" + scoreboardKey.format(DateTimeFormatter.ISO_LOCAL_DATE)
                + ") found " + gameMap.size() + " existing games.");

        try {
            Scoreboard scoreboard = objectMapper.readValue(sbs.getResponse(), Scoreboard.class);
            List<ScoreboardGame> scoreboardGames = scoreboard.unpackGames();
            logger.info("Unpacked " + scoreboardGames.size() + " from the scraped JSON " + sbs.getId());

            List<ScoreboardGame> oldGames = scoreboardGames.stream().filter(g -> gameMap.containsKey(g.getUid()))
                    .toList();
            List<ScoreboardGame> newGames = scoreboardGames.stream().filter(g -> !gameMap.containsKey(g.getUid()))
                    .toList();

            logger.info("For scoreboard scrape " + sbsId + " found " + scoreboardGames.size() + " games for scrape.");

            List<Game> toBeUpdated = oldGames.stream()
                    .flatMap(g -> makeGame(g, teamMap, LocalDateTime.now(), scoreboardKey, sbsId).stream()
                            .flatMap(h -> h.createUpdate(gameMap.get(g.getUid())).stream()))
                    .toList();
            List<Game> updatedGames = gameRepo.saveAllAndFlush(toBeUpdated);
            logger.info("For " + scoreboardKey + " " + updatedGames.size() + " games updated");
            // fixme
            Set<String> knownKeys = scoreboardGames.stream().filter(ScoreboardGame::isPlayable)
                    .map(ScoreboardGame::getUid).collect(Collectors.toSet());
            Set<String> toBeDeleted = gameMap.keySet().stream().filter(k -> !knownKeys.contains(k))
                    .collect(Collectors.toSet());
            int deleteCount = gameRepo.deleteAllByEspnIdIn(toBeDeleted);
            logger.info("For " + scoreboardKey + " " + deleteCount + " games deleted");

            List<Game> toBeInserted = newGames
                    .stream()
                    .map(s -> makeGame(s, teamMap, LocalDateTime.now(), scoreboardKey, sbsId))
                    .flatMap(Optional::stream).toList();
            List<Game> insertedGames = gameRepo.saveAllAndFlush(toBeInserted);
            logger.info("For " + scoreboardKey + " " + insertedGames.size() + " games inserted");
            return Optional
                    .of(new PublishResult(scoreboardKey, insertedGames.size(), updatedGames.size(), deleteCount, ""));

        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON for scoreboard scrape " + sbsId, e);
            return Optional.empty();
        }
    }

    private Optional<Game> makeGame(ScoreboardGame s, Map<String, Team> teams, LocalDateTime now, LocalDate sbKey,
            long id) {
        if (s.getSummary().toLowerCase().contains("canceled")) {
            logger.info("Skipping " + s.getUid() + " listed as canceled");
            return Optional.empty();
        }
        List<ScoreboardTeam> competitors = s.getCompetitors();
        if (competitors == null || competitors.size() != 2) {
            logger.info("Skipping " + s.getUid() + " missing or wrong number of competitors");
            return Optional.empty();
        }
        ScoreboardTeam t0 = competitors.get(0);
        if (!teams.containsKey(t0.getId())) {
            logger.info("Skipping " + s.getUid() + " team " + t0.getName() + " is unknown.");
            return Optional.empty();
        }
        ScoreboardTeam t1 = competitors.get(1);
        if (!teams.containsKey(t1.getId())) {
            logger.info("Skipping " + s.getUid() + " team " + t1.getName() + " is unknown.");
            return Optional.empty();
        }
        try {
            Game g = new Game();
            g.setPublishedAt(now);
            g.setScrapeSrcId(id);
            g.setScoreboardKey(sbKey);
            g.setDate(s.getDate().toLocalDate());
            g.setSeason(findSeasonBySeason(s.getSeason()));
            g.setEspnId(s.getUid());
            g.setLocation(StringUtils.truncate(s.getLocation(), 48));
            g.setNcaaTournament(s.getSeasonType().equalsIgnoreCase("3"));
            if (t0.getHomeAway().equalsIgnoreCase("home")) {
                setTeamInfo(s, teams, g, t0, t1);
            } else {
                setTeamInfo(s, teams, g, t1, t0);
            }
            if (s.getOdds() != null) {
                g.setOverUnder(s.getOdds().getOverUnder());
                g.setSpread(s.getOdds().getSpread());
            }
            return Optional.of(g);
        } catch (Exception e) {
            logger.error("Unexpected error creating game from ScoreboardGame", e);
            return Optional.empty();
        }
    }

    private static void setTeamInfo(ScoreboardGame s, Map<String, Team> teams, Game g, ScoreboardTeam t0,
            ScoreboardTeam t1) {
        g.setHomeTeam(teams.get(t0.getId()));
        g.setAwayTeam(teams.get(t1.getId()));
        if (s.getSummary().toLowerCase().contains("final")) {
            if (StringUtils.isNotBlank(t0.getScore()))
                g.setHomeScore(Integer.parseInt(t0.getScore()));
            if (StringUtils.isNotBlank(t1.getScore()))
                g.setAwayScore(Integer.parseInt(t1.getScore()));
            g.setNumPeriods(Integer.parseInt(s.getPeriod()));
        }
    }

    public EspnSeasonScrape findSeasonScrapeById(long id) {
        return seasonScrapeRepo.findById(id).orElseThrow();
    }

    @Scheduled(cron = "0 0 2,4 * * *", zone = "America/New_York")
    public void updateCurrentSeason() {
        Season currentSeason = repo.findFirstByOrderBySeasonDesc().orElseThrow();
        LocalDate today = LocalDate.now();
        if (currentSeason.includesDate(today)) {
            LocalDate start = today.minusDays(2);
            LocalDate end = today.plusDays(8);
            logger.info("Updating current season from " + start + " to " + end + " With timeout of 3600 seconds");
            Long newScrapeId = scrapeSeason(currentSeason, start, end, 3600L);
            schedulePublish(newScrapeId, 3600L);

        } else {
            logger.info("Current season " + currentSeason.getSeason() + " does not include today " + today
                    + ". Skipping update.");
        }
    }

    private void schedulePublish(Long newScrapeId, Long timeout) {
        threadPool.schedule(() -> {
            logger.info("Publishing current season scrape update [" + newScrapeId + "]");
            seasonScrapeRepo.findById(newScrapeId).ifPresentOrElse((ss) -> {
                if (ss.getStatus().equalsIgnoreCase("COMPLETED")) {
                    logger.info("Status:" + ss.getStatus() + ", completed at " + ss.getCompletedAt());
                    List<PublishResult> result = publishSeasonScrape(newScrapeId);
                    mailer.sendGamesUpdatedMessage(ss, result);
                } else {
                    logger.warn("Status:" + ss.getStatus());
                    if (ss.getCompletedAt() != null) {
                        logger.warn("Completed at " + ss.getCompletedAt());
                        logger.warn("Will not publish");
                    } else {
                        if (timeout > 0L) {
                            logger.warn("Will schedule publish again in 600 seconds");
                            schedulePublish(newScrapeId, timeout - 600);
                        } else {
                            logger.warn("Scrape has timed out and will not be published.");
                        }
                    }
                }
            }, () -> logger.error("Could not find season scrape with id " + newScrapeId));
        }, 600L, TimeUnit.SECONDS);
    }
}
