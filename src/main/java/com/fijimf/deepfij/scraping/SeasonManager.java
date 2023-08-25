package com.fijimf.deepfij.scraping;

import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.scrape.EspnSeasonScrape;
import com.fijimf.deepfij.db.model.scrape.EspnStandingsScrape;
import com.fijimf.deepfij.db.repo.schedule.SeasonRepo;
import com.fijimf.deepfij.db.repo.scrape.EspnSeasonScrapeRepo;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

@Component
public class SeasonManager {
    private final Logger logger = LoggerFactory.getLogger(SeasonManager.class);
    private final SeasonRepo repo;

    private final EspnSeasonScrapeRepo seasonScrapeRepo;
    private final StandingsScrapeManager standingsMgr;
    private final ScoreboardScrapeManager scoreboardMgr;

    private final ConcurrentMap<Long, ExecutorService> executors = new ConcurrentHashMap<>();
    public SeasonManager(SeasonRepo repo, EspnSeasonScrapeRepo seasonScrapeRepo, StandingsScrapeManager standingsMgr, ScoreboardScrapeManager scoreboardMgr) {
        this.repo = repo;
        this.seasonScrapeRepo = seasonScrapeRepo;
        this.standingsMgr = standingsMgr;
        this.scoreboardMgr = scoreboardMgr;
    }

    @PostConstruct
    public void init() {
        logger.info("Cleaning up scrape table on startup.");
        seasonScrapeRepo.findByCompletedAtIsNull().forEach(ss->{
            logger.info("Abandoning unfinished season scrape "+ss.getId());
            seasonScrapeRepo.saveAndFlush(ss.complete("ABANDONED"));
        });
        seasonScrapeRepo.findByCompletedAtIsNotNullAndStatus("RUNNING").forEach(ss->{
            logger.info("Setting scrape "+ss.getId()+" with completion at "+ ss.getCompletedAt()+" and status 'RUNNING' to 'COMPLETED'");
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

//    public Long scrapeSeason(Season season, String from, String to) {
//        LocalDate start = LocalDate.parse(from, DateTimeFormatter.ofPattern("yyyyMMdd"));
//        LocalDate end = LocalDate.parse(to, DateTimeFormatter.ofPattern("yyyyMMdd"));
//        return scrapeSeason(season, start, end);
//    }

    private Long scrapeSeason(Season season, LocalDate start, LocalDate end, Long timeout) {
        Random random = new Random();
        EspnSeasonScrape seasonScrape = seasonScrapeRepo.saveAndFlush(new EspnSeasonScrape(0L, season.getSeason(), start, end, LocalDateTime.now(), null, "STARTING"));
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executors.put(seasonScrape.getId(),executorService);
        start.datesUntil(end).forEach(d -> executorService.submit( () -> {
            long sleepDelay = random.nextLong(500L, 2000L);
            logger.info("Loading "+d+" - delay is "+sleepDelay+" ms");
            try {
                Thread.sleep(sleepDelay);
            } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
            }
            scrapeDate(d, seasonScrape.getId());
            seasonScrapeRepo.updateStatusById(seasonScrape.getId(),"RUNNING");

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
        if (executorService!=null){
            executorService.shutdownNow();
            seasonScrapeRepo.findById(id).ifPresent(s->updateSeasonScrapeStatus(s,"CANCELLED"));
        }
    }
    private void updateSeasonScrapeStatus(EspnSeasonScrape seasonScrape, String status) {
        seasonScrapeRepo.findById(seasonScrape.getId()).ifPresent(s -> {
            logger.info("Setting scrape "+seasonScrape.getId()+" from "+s.getStatus()+" to "+status);
            if (s.getCompletedAt()!=null) {
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
        LocalDate start = StringUtils.isNotBlank(from)?LocalDate.parse(from,DateTimeFormatter.ofPattern("yyyyMMdd")):defaultStartDate(year);
        LocalDate end = StringUtils.isNotBlank(to)?LocalDate.parse(to,DateTimeFormatter.ofPattern("yyyyMMdd")):defaultEndDate(year);
        Long timeout = StringUtils.isNotBlank(timeOutSec)?Long.parseLong(timeOutSec):3600L;
        return scrapeSeason(season, start, end, timeout);
    }

    public List<EspnSeasonScrape> findSeasonScrapesBySeason(Season season) {
        return seasonScrapeRepo.findAllBySeasonOrderByStartedAt(season.getSeason());
    }

    public Season publishSeasonScrape(long seasonId, long seasonScrapeId) {
        logger.error("Not implemented yet");
        return null;
    }

    public EspnSeasonScrape findSeasonScrapeById(long id) {
       return seasonScrapeRepo.findById(id).orElseThrow();
    }
}
