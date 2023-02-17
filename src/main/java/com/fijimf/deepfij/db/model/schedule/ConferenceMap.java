package com.fijimf.deepfij.db.model.schedule;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "conference_maps")
public class ConferenceMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "season_id")
    private Season season;
    @Column(name = "conference_id")
    private long conferenceId;
    @Column(name = "team_id")
    private long teamId;
    @Column(name = "scrape_src_id")
    private long scrapeSrcId;
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    public ConferenceMap() {
    }

    public ConferenceMap(long id, Season season, long conferenceId, long teamId, long scrapeSrcId, LocalDateTime publishedAt) {
        this.id = id;
        this.season = season;
        this.conferenceId = conferenceId;
        this.teamId = teamId;
        this.scrapeSrcId = scrapeSrcId;
        this.publishedAt = publishedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public long getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(long conferenceId) {
        this.conferenceId = conferenceId;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
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
}
