package com.fijimf.deepfij.db.model.schedule;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String key;
    @Column(unique = true)
    private String name;
    private String nickname;
    @Column(name = "long_name", unique = true)
    private String longName;
    @Column(name = "alt_name_1")
    private String altName1;
    @Column(name = "alt_name_2")
    private String altName2;
    private String color;
    @Column(name = "alt_color")
    private String altColor;
    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "espn_id", unique = true)
    private String espnId;
    @Column(name = "scrape_src_id")
    private long scrapeSrcId;
    @Column(name = "published_at")
    private LocalDateTime publishedAt;


    public Team() {
    }

    public Team(long id, String key, String name, String nickname, String longName, String altName1, String altName2, String color, String altColor, String logoUrl, String espnId, long scrapeSrcId, LocalDateTime publishedAt) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.nickname = nickname;
        this.longName = longName;
        this.altName1 = altName1;
        this.altName2 = altName2;
        this.color = color;
        this.altColor = altColor;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getAltName1() {
        return altName1;
    }

    public void setAltName1(String altName1) {
        this.altName1 = altName1;
    }

    public String getAltName2() {
        return altName2;
    }

    public void setAltName2(String altName2) {
        this.altName2 = altName2;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getAltColor() {
        return altColor;
    }

    public void setAltColor(String altColor) {
        this.altColor = altColor;
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

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", longName='" + longName + '\'' +
                ", altName1='" + altName1 + '\'' +
                ", altName2='" + altName2 + '\'' +
                ", color='" + color + '\'' +
                ", altColor='" + altColor + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", espnId='" + espnId + '\'' +
                ", scrapeSrcId=" + scrapeSrcId +
                ", publishedAt=" + publishedAt +
                '}';
    }
}
