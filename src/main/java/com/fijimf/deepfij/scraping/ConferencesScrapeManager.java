package com.fijimf.deepfij.scraping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fijimf.deepfij.db.model.schedule.Conference;
import com.fijimf.deepfij.db.model.scrape.EspnConferencesScrape;
import com.fijimf.deepfij.db.repo.schedule.ConferenceRepo;
import com.fijimf.deepfij.db.repo.scrape.EspnConferencesScrapeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;

@Component
public class ConferencesScrapeManager {
    public static final Logger logger = LoggerFactory.getLogger(ConferencesScrapeManager.class);
    public static final String conferenceListUrl = "https://site.web.api.espn.com/apis/site/v2/sports/basketball/mens-college-basketball/scoreboard/conferences";
    private final ConferenceRepo repo;
    private final EspnConferencesScrapeRepo scrapeRepo;
    private final ObjectMapper objectMapper;


    public ConferencesScrapeManager(ConferenceRepo repo, EspnConferencesScrapeRepo scrapeRepo) {
        this.repo = repo;
        this.scrapeRepo = scrapeRepo;
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public EspnConferencesScrape scrape() {
        LocalDateTime start = LocalDateTime.now();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(conferenceListUrl))
                    .timeout(Duration.of(10, SECONDS))
                    .GET()
                    .build();
            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            int status = response.statusCode();
            String digest = DigestUtils.md5DigestAsHex(response.body());
            long duration = ChronoUnit.MILLIS.between(start, LocalDateTime.now());
            logger.info("Scrape response Code: " + response.statusCode() + " Bytes: " + response.body().length + " Duration: " + duration);
            try {
                Conferences conferences = objectMapper.readValue(response.body(), Conferences.class);
                try {
                    return scrapeRepo.saveAndFlush(new EspnConferencesScrape(0L, conferenceListUrl
                            , start, duration, status, objectMapper.writerFor(Conferences.class).writeValueAsString(conferences), digest, "OK"));
                } catch (JsonProcessingException e) {
                    logger.error("", e);
                    return scrapeRepo.saveAndFlush(new EspnConferencesScrape(0L, conferenceListUrl
                            , start, duration, status, null, digest, "FAILED SERIALIZING"));

                }
            } catch (IOException e) {
                logger.error("", e);
                return scrapeRepo.saveAndFlush(new EspnConferencesScrape(0L, conferenceListUrl
                        , start, duration, status, null, digest, "FAILED DESERIALIZING"));
            }

        } catch (Exception e) {
            logger.error("", e);
            return scrapeRepo.saveAndFlush(new EspnConferencesScrape(0L, conferenceListUrl
                    , start, 0L, -100, null, null, "HTTPREQUEST FAILED"));
        }
    }

    public void publishConferences(long id) {
        scrapeRepo.findById(id).ifPresent(s -> {
            try {
                Conferences conferences1 = objectMapper.readValue(s.getResponse(), Conferences.class);
                List<Conference> conferences = conferences1.values();
                conferences.forEach(c -> {
                    Optional<Conference> optionalConference = repo.findByEspnIdEquals(c.getEspnId());
                    if (optionalConference.isPresent()) {
                        Conference conference = optionalConference.get();
                        conference.setAltName(c.getAltName());
                        conference.setName(c.getName());
                        conference.setKey(c.getKey());
                        conference.setLogoUrl(c.getLogoUrl());
                        conference.setScrapeSrcId(id);
                        conference.setPublishedAt(LocalDateTime.now());
                        repo.saveAndFlush(conference);
                    } else {
                        repo.saveAndFlush(c);
                    }
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void deleteAll() {
        repo.deleteAll();
    }
}
