package com.fijimf.deepfij.db.model.scrape;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "espn_conferences_scrape")
public class EspnConferencesScrape {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;
        private String url;
        @Column(name="retrieved_at")
        private LocalDateTime retrievedAt;
        @Column(name="response_time_ms")
        private Long responseTimeMs;
        @Column(name="response_code")
        private Integer responseCode;

        private String response;
        private String digest;
        private String status;

    public EspnConferencesScrape() {
    }

    public EspnConferencesScrape(long id, String url, LocalDateTime retrievedAt, Long responseTimeMs, Integer responseCode, String response, String digest,  String status) {
        this.id = id;
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
}
