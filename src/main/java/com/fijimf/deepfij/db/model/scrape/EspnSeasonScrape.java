package com.fijimf.deepfij.db.model.scrape;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "espn_season_scrape")
public class EspnSeasonScrape {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int season;
    @Column(name="from_date")
    private LocalDate from;
    @Column(name="to_date")
    private LocalDate to;

    @Column(name="started_at")
    private LocalDateTime startedAt;
    @Column(name="completed_at")
    private LocalDateTime completedAt;

    private String status;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name="espn_season_scrape_id")
    private List<EspnScoreboardScrape> scoreboardScrapes;


    public EspnSeasonScrape() {
    }

    public EspnSeasonScrape(long id, int season, LocalDate from, LocalDate to, LocalDateTime startedAt, LocalDateTime completedAt, String status) {
        this.id = id;
        this.season = season;
        this.from = from;
        this.to = to;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private EspnSeasonScrape complete(String status) {
        setCompletedAt(LocalDateTime.now());
        setStatus(status);
        return this;
    }

    public EspnSeasonScrape success() {
        return complete("SUCCESS");
    }

    public EspnSeasonScrape cancel() {
        return complete("CANCELLED");
    }

    public EspnSeasonScrape error() {
        return complete("ERROR");
    }

    public EspnSeasonScrape timeout() {
        return complete("TIMEOUT");
    }

    public LocalDate fromDate() {
        LocalDate d = LocalDate.of(season - 1, 11, 1);
        return ((from == null) || from.isBefore(d)) ? d : from;
    }

    public LocalDate toDate() {
        LocalDate d = LocalDate.of(season, 4, 30);
        return ((to == null) || to.isAfter(d)) ? d : to;
    }

    public List<EspnScoreboardScrape> getScoreboardScrapes() {
        return scoreboardScrapes;
    }

    public void setScoreboardScrapes(List<EspnScoreboardScrape> scoreboardScrapes) {
        this.scoreboardScrapes = scoreboardScrapes;
    }

    public int okCount(){
        if (scoreboardScrapes==null) {
            return 0;
        } else {
            return (int) scoreboardScrapes.stream().filter(s -> s.getResponseCode() == 200).count();
        }
    }
    public int errCount(){
        if (scoreboardScrapes==null) {
            return 0;
        } else {
            return (int) scoreboardScrapes.stream().filter(s -> s.getResponseCode() != 200).count();
        }
    }
}
