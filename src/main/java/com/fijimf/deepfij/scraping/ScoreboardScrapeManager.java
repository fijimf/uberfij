package com.fijimf.deepfij.scraping;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fijimf.deepfij.db.model.scrape.EspnScoreboardScrape;
import com.fijimf.deepfij.db.model.scrape.EspnSeasonScrape;
import com.fijimf.deepfij.db.repo.scrape.EspnScoreboardScrapeRepo;
import com.fijimf.deepfij.db.repo.scrape.EspnSeasonScrapeRepo;


@Component
public class ScoreboardScrapeManager {
    public static final Logger logger = LoggerFactory.getLogger(ScoreboardScrapeManager.class);

    private final static String URL = "https://site.web.api.espn.com/apis/v2/scoreboard/header?sport=basketball&league=mens-college-basketball&limit=200&groups=50&dates=";

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final EspnScoreboardScrapeRepo repo;
    private final EspnSeasonScrapeRepo scrRepo;

    public ScoreboardScrapeManager(EspnScoreboardScrapeRepo repo, EspnSeasonScrapeRepo scrRepo) {
        this.repo = repo;
        this.scrRepo = scrRepo;
    }

    public String scoreboardUrl(LocalDate d) {
        return URL + d.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    @Transactional
    public void scrapeScoreboardDate(LocalDate d, Long seasonScrapeId) {
        String scoreboardUrl = scoreboardUrl(d);
        EspnSeasonScrape espnSeasonScrape = scrRepo.findById(seasonScrapeId).orElseThrow(RuntimeException::new);
        LocalDateTime start = LocalDateTime.now();
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(scoreboardUrl)).timeout(Duration.of(10, SECONDS)).GET().build();
            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            int status = response.statusCode();
            long duration = ChronoUnit.MILLIS.between(start, LocalDateTime.now());
            logger.info("Scrape response Code: " + response.statusCode() + " Bytes: " + response.body().length + " Duration: " + duration);
            try {
                Scoreboard scoreboard = objectMapper.readValue(response.body(), Scoreboard.class);
                try {
                    repo.saveAndFlush(new EspnScoreboardScrape(0L, espnSeasonScrape, d, "ODDS", scoreboardUrl, start, duration, status, objectMapper.writerFor(Scoreboard.class).writeValueAsString(scoreboard), scoreboard.numberOfGames()));
                } catch (JsonProcessingException e) {
                    logger.error("Failed serialization " + d, e);
                    repo.saveAndFlush(new EspnScoreboardScrape(0L, espnSeasonScrape, d, "ODDS", scoreboardUrl, start, duration, status, null, 0));
                }
            } catch (IOException e) {
                logger.error("IO Exception", e);
                repo.saveAndFlush(new EspnScoreboardScrape(0L, espnSeasonScrape, d, "ODDS", scoreboardUrl, start, duration, status, null, 0));
            }

        } catch (Exception e) {
            logger.error("", e);
            repo.saveAndFlush(new EspnScoreboardScrape(0L, espnSeasonScrape, d, "ODDS", scoreboardUrl, start, -1L, -200, null, 0));
        }
    }
}
