package com.fijimf.deepfij.db.model.schedule;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Arrays;

@Entity
@Table(name = "conference")
public class Conference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String key;
    @Column(unique = true)
    private String name;
    @Column(name = "alt_name")
    private String altName;
    @Column(name = "logo_url")
    private String logoUrl;
    @Column(name = "espn_id", unique = true)
    private String espnId;
    @Column(name = "scrape_src_id")
    private long scrapeSrcId;
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    public Conference() {
    }

    public Conference(long id, String key, String name, String altName, String logoUrl, String espnId, long scrapeSrcId, LocalDateTime publishedAt) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.altName = altName;
        this.logoUrl = logoUrl;
        this.espnId = espnId;
        this.scrapeSrcId = scrapeSrcId;
        this.publishedAt = publishedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAltName() {
        return altName;
    }

    public void setAltName(String altName) {
        this.altName = altName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getEspnId() {
        return espnId;
    }

    public void setEspnId(String espnId) {
        this.espnId = espnId;
    }

    public long getScrapeSrcId() {
        return scrapeSrcId;
    }

    public void setScrapeSrcId(long scrapeSrcId) {
        this.scrapeSrcId = scrapeSrcId;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public boolean isValid() {
        return !Arrays.stream(new String[]{"top-25", "all"}).toList().contains(key);
    }
}
