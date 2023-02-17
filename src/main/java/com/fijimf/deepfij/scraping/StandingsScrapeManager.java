package com.fijimf.deepfij.scraping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fijimf.deepfij.db.model.schedule.Conference;
import com.fijimf.deepfij.db.model.schedule.ConferenceMap;
import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.schedule.Team;
import com.fijimf.deepfij.db.model.scrape.EspnConferencesScrape;
import com.fijimf.deepfij.db.model.scrape.EspnStandingsScrape;
import com.fijimf.deepfij.db.repo.schedule.ConferenceMappingRepo;
import com.fijimf.deepfij.db.repo.schedule.ConferenceRepo;
import com.fijimf.deepfij.db.repo.schedule.SeasonRepo;
import com.fijimf.deepfij.db.repo.schedule.TeamRepo;
import com.fijimf.deepfij.db.repo.scrape.EspnStandingsScrapeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.Map;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;

@Component
public class StandingsScrapeManager {
    private final static String URL = "https://site.api.espn.com/apis/v2/sports/basketball/mens-college-basketball/standings?season=";
    private final static Logger logger = LoggerFactory.getLogger(StandingsScrapeManager.class);

    private EspnStandingsScrapeRepo scrapeRepo;
    private ConferenceMappingRepo conferenceMappingRepo;
    private SeasonRepo seasonRepo;
    private TeamRepo teamRepo;
    private ConferenceRepo conferenceRepo;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public StandingsScrapeManager(EspnStandingsScrapeRepo scrapeRepo, ConferenceMappingRepo conferenceMappingRepo, SeasonRepo seasonRepo, TeamRepo teamRepo, ConferenceRepo conferenceRepo) {
        this.scrapeRepo = scrapeRepo;
        this.conferenceMappingRepo = conferenceMappingRepo;

        this.seasonRepo = seasonRepo;
        this.teamRepo = teamRepo;
        this.conferenceRepo = conferenceRepo;
    }

    public EspnStandingsScrape scrape(int season) {
        String seasonUrl = URL + season;
        LocalDateTime start = LocalDateTime.now();
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(seasonUrl)).timeout(Duration.of(10, SECONDS)).GET().build();
            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            int status = response.statusCode();
            String digest = DigestUtils.md5DigestAsHex(response.body());
            long duration = ChronoUnit.MILLIS.between(start, LocalDateTime.now());
            logger.info("Scrape response Code: " + response.statusCode() + " Bytes: " + response.body().length + " Duration: " + duration);
            try {
                Standings teams = objectMapper.readValue(response.body(), Standings.class);
                try {
                    return scrapeRepo.saveAndFlush(new EspnStandingsScrape(0L, season, seasonUrl, start, duration, status, objectMapper.writerFor(Standings.class).writeValueAsString(teams), digest, "OK"));
                } catch (JsonProcessingException e) {
                    logger.error("", e);
                    return scrapeRepo.saveAndFlush(new EspnStandingsScrape(0L, season, seasonUrl, start, duration, status, null, digest, "FAILED SERIALIZING"));

                }
            } catch (IOException e) {
                logger.error("", e);
                return scrapeRepo.saveAndFlush(new EspnStandingsScrape(0L, season, seasonUrl, start, duration, status, null, digest, "FAILED DESERIALIZING"));
            }

        } catch (Exception e) {
            logger.error("", e);
            return scrapeRepo.saveAndFlush(new EspnStandingsScrape(0L, season, seasonUrl, start, 0L, -100, null, null, "HTTPREQUEST FAILED"));
        }
    }

    @Transactional
    public void publishConferenceMap(long id, boolean cleanUp) {
        publishConferenceMap(scrapeRepo.findById(id).orElseThrow(), cleanUp);
    }

    @Transactional
    public void publishConferenceMap(EspnStandingsScrape scrape, boolean cleanUp) {
        Season season = seasonRepo.findBySeason(scrape.getSeason()).orElseThrow();
        try {
            Standings standings = objectMapper.readValue(scrape.getResponse(), Standings.class);
            Map<String, List<String>> confMap = standings.mapValues();
            confMap.keySet().forEach(c -> {
                Conference conf = conferenceRepo.findByEspnIdEquals(c).orElseThrow();
                confMap.get(c).stream().forEach(t -> {
                    Team team = teamRepo.findByEspnIdEquals(t).orElseThrow();
                    Optional<ConferenceMap> optMapping = conferenceMappingRepo.findBySeasonIdAndTeamId(season.getId(), team.getId());
                    if (optMapping.isPresent()) {
                        ConferenceMap conferenceMap = optMapping.get();
                        conferenceMap.setConferenceId(conf.getId());
                        conferenceMap.setScrapeSrcId(scrape.getId());
                        conferenceMap.setPublishedAt(LocalDateTime.now());
                        conferenceMappingRepo.saveAndFlush(conferenceMap);
                    } else {
                        ConferenceMap conferenceMap = new ConferenceMap(0L, season, conf.getId(), team.getId(), scrape.getId(), LocalDateTime.now());
                        conferenceMappingRepo.saveAndFlush(conferenceMap);
                    }
                });
            });
            if (cleanUp) {
                conferenceMappingRepo.deleteBySeasonIdAndScrapeSrcIdNot(season.getId(), scrape.getId());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public String showRawStandingsScrape(long id) {
        return scrapeRepo.findById(id).map(EspnStandingsScrape::getResponse).orElse("");
    }

    public List<ConferenceMap> findAllConfMaps() {
        return conferenceMappingRepo.findAll();
    }

    public List<EspnStandingsScrape> findAllStandingsScrapes() {
        return scrapeRepo.findAll();
    }
}
