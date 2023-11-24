package com.fijimf.deepfij.scraping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fijimf.deepfij.db.model.schedule.Team;
import com.fijimf.deepfij.db.model.scrape.EspnTeamsScrape;
import com.fijimf.deepfij.db.repo.schedule.TeamRepo;
import com.fijimf.deepfij.db.repo.scrape.EspnTeamsScrapeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;

@Component
public class TeamsScrapeManager {
    public static final Logger logger = LoggerFactory.getLogger(TeamsScrapeManager.class);
    public static final String teamsListUrl = "https://site.api.espn.com/apis/site/v2/sports/basketball/mens-college-basketball/teams?limit=500";
    private final TeamRepo repo;
    private final EspnTeamsScrapeRepo scrapeRepo;
    private final ObjectMapper objectMapper;


    public TeamsScrapeManager(TeamRepo repo, EspnTeamsScrapeRepo scrapeRepo) {
        this.repo = repo;
        this.scrapeRepo = scrapeRepo;
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public EspnTeamsScrape scrape() {
        LocalDateTime start = LocalDateTime.now();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(teamsListUrl))
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
                Teams teams = objectMapper.readValue(response.body(), Teams.class);
                try {
                    return scrapeRepo.saveAndFlush(new EspnTeamsScrape(0L, teamsListUrl
                            , start, duration, status, objectMapper.writerFor(Teams.class).writeValueAsString(teams), digest, "OK"));
                } catch (JsonProcessingException e) {
                    logger.error("", e);
                    return scrapeRepo.saveAndFlush(new EspnTeamsScrape(0L, teamsListUrl
                            , start, duration, status, null, digest, "FAILED SERIALIZING"));

                }
            } catch (IOException e) {
                logger.error("", e);
                return scrapeRepo.saveAndFlush(new EspnTeamsScrape(0L, teamsListUrl
                        , start, duration, status, null, digest, "FAILED DESERIALIZING"));
            }

        } catch (Exception e) {
            logger.error("", e);
            return scrapeRepo.saveAndFlush(new EspnTeamsScrape(0L, teamsListUrl
                    , start, 0L, -100, null, null, "HTTPREQUEST FAILED"));
        }
    }

    @Transactional
    public void publishTeams(long id, boolean cleanUp) {
        scrapeRepo.findById(id).ifPresent(s -> {
            try {
                List<Team> teams = objectMapper.readValue(s.getResponse(), Teams.class).values();
                teams.forEach(t -> {
                    logger.info(t.toString());
                    Optional<Team> optionalTeam = repo.findByEspnIdEquals(t.getEspnId());
                    if (optionalTeam.isPresent()) {
                        Team team = optionalTeam.get();
                        team.setKey(t.getKey());
                        team.setName(t.getName());
                        team.setLongName(t.getLongName());
                        team.setNickname(t.getNickname());
                        team.setAltName1(t.getAltName1());
                        team.setAltName2(t.getAltName2());
                        team.setColor(t.getColor());
                        team.setAltColor(t.getAltColor());
                        team.setLogoUrl(t.getLogoUrl());
                        team.setScrapeSrcId(id);
                        team.setPublishedAt(LocalDateTime.now());
                        repo.saveAndFlush(team);
                    } else {
                        t.setScrapeSrcId(id);
                        repo.saveAndFlush(t);
                    }
                });
                if (cleanUp) {
                    repo.deleteByScrapeSrcIdNot(id);
                }

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public String showRawConferencesScrape(long id) {
        return scrapeRepo.findById(id).map(EspnTeamsScrape::getResponse).orElse("");
    }

    public List<Team> findAllTeams() {
        return repo.findAll();
    }

    public List<EspnTeamsScrape> findAllTeamScrapes() {
        return scrapeRepo.findAll();
    }
}
