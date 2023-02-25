package com.fijimf.deepfij.db.model.schedule;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Entity
@Table(name = "conference_maps")
public class ConferenceMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "season_id", updatable = false, insertable = true)
    private Season season;

    @OneToOne
    @JoinColumn(name = "conference_id", referencedColumnName = "id")
    private Conference conference;

    @OneToOne
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private Team team;
    @Column(name = "scrape_src_id")
    private long scrapeSrcId;
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    public ConferenceMap() {
    }

    public ConferenceMap(long id, Season season, Conference conference, Team team, long scrapeSrcId, LocalDateTime publishedAt) {
        this.id = id;
        this.season = season;
        this.conference = conference;
        this.team = team;
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

    public Conference getConference() {
        return conference;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
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
