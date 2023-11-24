package com.fijimf.deepfij.db.model.scrape;

import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@Entity
@Table(name = "espn_standings_scrape")
public class EspnStandingsScrape {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int season;
    private String url;
    @Column(name = "retrieved_at")
    private LocalDateTime retrievedAt;
    @Column(name = "response_time_ms")
    private Long responseTimeMs;
    @Column(name = "response_code")
    private Integer responseCode;

    private String response;

    private String digest;
    private String status;

    public EspnStandingsScrape() {
    }

    public EspnStandingsScrape(long id, int season, String url, LocalDateTime retrievedAt, Long responseTimeMs, Integer responseCode, String response, String digest, String status) {
        this.id = id;
        this.season = season;
        this.url = url;
        this.retrievedAt = retrievedAt;
        this.responseTimeMs = responseTimeMs;
        this.responseCode = responseCode;
        this.response = response;
        this.digest = digest;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public String displayUrl() {
        return url.length() > 32 ? StringUtils.truncate(url, 32) + "..." : url;
    }

}
