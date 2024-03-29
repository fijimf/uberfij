package com.fijimf.deepfij.db.model.scrape;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "espn_scoreboard_scrape")
public class EspnScoreboardScrape {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "espn_season_scrape_id")
    private EspnSeasonScrape espnSeasonScrape;
    @Column(name = "scoreboard_key")
    private LocalDate scoreboardKey;

    private String flavor;
    private String url;

    @Column(name = "retrieved_at")
    private LocalDateTime retrievedAt;

    @Column(name = "response_time_ms")
    private Long responseTimeMs;
    @Column(name = "response_code")
    private Integer responseCode;

    private String response;
    @Column(name = "number_of_games")
    private Integer numberOfGames;

    public EspnScoreboardScrape() {
    }

    public EspnScoreboardScrape(long id, EspnSeasonScrape espnSeasonScrape, LocalDate scoreboardKey, String flavor, String url, LocalDateTime retrievedAt, Long responseTimeMs, Integer responseCode, String response, Integer numberOfGames) {
        this.id = id;
        this.espnSeasonScrape = espnSeasonScrape;
        this.scoreboardKey = scoreboardKey;
        this.flavor = flavor;
        this.url = url;
        this.retrievedAt = retrievedAt;
        this.responseTimeMs = responseTimeMs;
        this.responseCode = responseCode;
        this.response = response;
        this.numberOfGames = numberOfGames;
    }

    public EspnScoreboardScrape(EspnSeasonScrape espnSeasonScrape, LocalDate scoreboardKey, String flavor, String url) {
        this.id = 0L;
        this.espnSeasonScrape = espnSeasonScrape;
        this.scoreboardKey = scoreboardKey;
        this.flavor = flavor;
        this.url = url;
        this.retrievedAt = null;
        this.responseTimeMs = null;
        this.responseCode = null;
        this.response = null;
        this.numberOfGames = null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public EspnSeasonScrape getEspnSeasonScrape() {
        return espnSeasonScrape;
    }

    public void setEspnSeasonScrape(EspnSeasonScrape espnSeasonScrape) {
        this.espnSeasonScrape = espnSeasonScrape;
    }

    public LocalDate getScoreboardKey() {
        return scoreboardKey;
    }

    public void setScoreboardKey(LocalDate scoreboardKey) {
        this.scoreboardKey = scoreboardKey;
    }

    public String getFlavor() {
        return flavor;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getRetrievedAt() {
        return retrievedAt;
    }

    public void setRetrievedAt(LocalDateTime retrievedAt) {
        this.retrievedAt = retrievedAt;
    }

    public Long getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Long responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Integer getNumberOfGames() {
        return numberOfGames;
    }

    public void setNumberOfGames(Integer numberOfGameIds) {
        this.numberOfGames = numberOfGameIds;
    }
}
