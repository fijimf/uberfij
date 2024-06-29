package com.fijimf.deepfij.scraping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fijimf.deepfij.db.model.scrape.EspnScoreboardScrape;
import com.fijimf.deepfij.db.model.scrape.EspnSeasonScrape;
import com.fijimf.deepfij.db.repo.scrape.EspnScoreboardScrapeRepo;
import com.fijimf.deepfij.db.repo.scrape.EspnSeasonScrapeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Component
public class ScoreboardScrapeManager {
    public static final Logger logger = LoggerFactory.getLogger(ScoreboardScrapeManager.class);

    private final static String URL = "https://site.web.api.espn.com/apis/v2/scoreboard/header?sport=basketball&league=mens-college-basketball&limit=200&groups=50&dates=";

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final EspnScoreboardScrapeRepo repo;
    private final EspnSeasonScrapeRepo scrRepo;
    private final ScoreboardHttpClient scoreboardHttpClient;

    public ScoreboardScrapeManager(EspnScoreboardScrapeRepo repo, EspnSeasonScrapeRepo scrRepo, ScoreboardHttpClient scoreboardHttpClient) {
        this.repo = repo;
        this.scrRepo = scrRepo;
        this.scoreboardHttpClient = scoreboardHttpClient;
    }

    @Transactional
    public void scrapeScoreboardDate(LocalDate date, Long seasonScrapeId, String flavor) throws InterruptedException {
        EspnSeasonScrape espnSeasonScrape = scrRepo.findById(seasonScrapeId).orElseThrow(RuntimeException::new);
        LocalDateTime start = LocalDateTime.now();
        try {
            HttpResponse<byte[]> response = scoreboardHttpClient.retrieveScoreboardData(date, flavor);
            processResponse(response, espnSeasonScrape, date, flavor, start);
        } catch (IOException e){
            logger.error("IOException processing "+date+" skipping",e);
            saveScrapeResult(espnSeasonScrape, date, "", "", start, -1L, -199, null, 0);
        } catch (URISyntaxException e) {
            logger.error("Bad URI "+e.getInput()+" skipping", e);
            saveScrapeResult(espnSeasonScrape, date, "", "", start, -1L, -299,"", 0);
        }
    }

    private void processResponse(HttpResponse<byte[]> response, EspnSeasonScrape espnSeasonScrape, LocalDate date, String flavor,  LocalDateTime retrievedAt) {

        String url=scoreboardHttpClient.getUrl(date, flavor);
        String responseString = null;
        int numberOfGames = 0;
        Long responseTimeMs = ChronoUnit.MILLIS.between(retrievedAt, LocalDateTime.now());
        try {
            Scoreboard scoreboard = objectMapper.readValue(response.body(), Scoreboard.class);
            responseString = objectMapper.writerFor(Scoreboard.class).writeValueAsString(scoreboard);
            numberOfGames = scoreboard.numberOfGames();
        } catch (JsonProcessingException e) {
            logger.error("Failed serialization/deserialization " + date, e);
        } catch (IOException e) {
            logger.error("IOException processing " + date, e);
        }
        saveScrapeResult(espnSeasonScrape, date, flavor, url, retrievedAt, responseTimeMs, response.statusCode(), responseString, numberOfGames);
    }

    private void saveScrapeResult(EspnSeasonScrape espnSeasonScrape, LocalDate date, String flavor, String url, LocalDateTime retrievedAt, Long responseTimeMs, int statusCode, String response, int numberOfGames) {
        repo.saveAndFlush(new EspnScoreboardScrape(0L, espnSeasonScrape, date, flavor, url, retrievedAt, responseTimeMs, statusCode, response, numberOfGames));
    }
}
