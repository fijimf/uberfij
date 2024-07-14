package com.fijimf.deepfij.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class GameSnapshot {
    private final Long id;
    private final String slug;
    private final LocalDate date;
    private  final TeamSnapshot homeSnapshot;
    private final Integer homeScore;
    private final TeamSnapshot awaySnapshot;
    private final Integer awayScore;
    private final Double spread;
    private final Double overUnder;

    public GameSnapshot(Long id, String slug, LocalDate date, TeamSnapshot homeSnapshot, Integer homeScore, TeamSnapshot awaySnapshot, Integer awayScore, Double spread, Double overUnder) {
        this.id = id;
        this.slug=slug;
        this.date = date;
        this.homeSnapshot = homeSnapshot;
        this.homeScore = homeScore;
        this.awaySnapshot = awaySnapshot;
        this.awayScore = awayScore;
        this.spread = spread;
        this.overUnder = overUnder;
    }

    public Long getId() {
        return id;
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    public LocalDate getDate() {
        return date;
    }

    public TeamSnapshot getHomeSnapshot() {
        return homeSnapshot;
    }

    public Integer getHomeScore() {
        return homeScore;
    }

    public TeamSnapshot getAwaySnapshot() {
        return awaySnapshot;
    }

    public Integer getAwayScore() {
        return awayScore;
    }

    public Double getSpread() {
        return spread;
    }

    public Double getOverUnder() {
        return overUnder;
    }

    public String getSlug() {
        return slug;
    }
}
