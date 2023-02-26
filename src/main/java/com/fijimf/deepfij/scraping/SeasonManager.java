package com.fijimf.deepfij.scraping;

import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.scrape.EspnSeasonScrape;
import com.fijimf.deepfij.db.model.scrape.EspnStandingsScrape;
import com.fijimf.deepfij.db.repo.schedule.SeasonRepo;
import com.fijimf.deepfij.db.repo.scrape.EspnSeasonScrapeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class SeasonManager {
    private final Logger logger = LoggerFactory.getLogger(SeasonManager.class);
    private final SeasonRepo repo;

    private final EspnSeasonScrapeRepo seasonScrapeRepo;
    private final StandingsScrapeManager standingsMgr;
    private final ScoreboardScrapeManager scoreboardMgr;

    public SeasonManager(SeasonRepo repo, EspnSeasonScrapeRepo seasonScrapeRepo, StandingsScrapeManager standingsMgr, ScoreboardScrapeManager scoreboardMgr) {
        this.repo = repo;
        this.seasonScrapeRepo = seasonScrapeRepo;
        this.standingsMgr = standingsMgr;
        this.scoreboardMgr = scoreboardMgr;
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

    public Long scrapeSeason(Long seasonId) {
        Optional<Season> optionalSeason = repo.findById(seasonId);
        return optionalSeason.map(this::scrapeSeason).orElse(-1L);
    }

    private Long scrapeSeason(Season season) {
        int yyyy = season.getSeason();
        LocalDate start = LocalDate.of(yyyy - 1, 11, 1);
        LocalDate end = LocalDate.of(yyyy, 4, 30);
        return scrapeSeason(yyyy, start, end);
    }

    private Long scrapeSeason(int yyyy, LocalDate start, LocalDate end) {
        Random random = new Random();
        EspnSeasonScrape seasonScrape = seasonScrapeRepo.saveAndFlush(new EspnSeasonScrape(0L, yyyy, start, end, LocalDateTime.now(), null, "STARTING"));
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        start.datesUntil(end).forEach(d -> {
            executorService.submit( () -> {
                long sleepDelay = random.nextLong(3000L, 9000L);
                logger.info("Loading "+d+" - delay is "+sleepDelay+" ms");
                try {
                    Thread.sleep(sleepDelay);
                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
                }
                scrapeDate(d, seasonScrape.getSeason(), seasonScrape.getId());
            });
        });

        executorService.shutdown();
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                boolean b = executorService.awaitTermination(1, TimeUnit.HOURS);
                if (b) {
                    updateSeasonScrapeStatus(seasonScrape, "COMPLETED");
                } else {
                    updateSeasonScrapeStatus(seasonScrape, "TIMED OUT");
                }
            } catch (InterruptedException e) {
                updateSeasonScrapeStatus(seasonScrape, "ABORTED");
            }

        });
        return seasonScrape.getId();
    }

    private void updateSeasonScrapeStatus(EspnSeasonScrape seasonScrape, String status) {
        seasonScrapeRepo.findById(seasonScrape.getId()).ifPresent(s -> {
            s.setCompletedAt(LocalDateTime.now());
            s.setStatus(status);
            seasonScrapeRepo.saveAndFlush(s);
        });
    }

    private void scrapeDate(LocalDate date, int season, long seasonScrapeId) {
        scoreboardMgr.scrapeScoreboardDate(date, seasonScrapeId);
    }

    public Long scrapeSeasonByYear(int year) {
        return scrapeSeason(findSeasonBySeason(year).getId());
    }
}
