package com.fijimf.deepfij.db.model.schedule;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "games") //FIXME
public class Game {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "season_id")
    private Season season;
    @PrimaryKeyJoinColumn(name = "home_team_id")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Team homeTeam;
    @PrimaryKeyJoinColumn(name = "away_team_id")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Team awayTeam;
    @Column(name = "home_score")
    private Integer homeScore;
    @Column(name = "away_score")
    private Integer awayScore;
    @Column(name = "num_periods")
    private Integer numPeriods;
    @Column(name = "is_neutral_site")
    private Boolean isNeutral_site;
    private String location;
    private Double spread;
    @Column(name = "over_under")
    private Double overUnder;
    @Column(name = "is_conf_tournament")
    private Boolean isConfTournament;
    @Column(name = "is_ncaa_tournament")
    private Boolean isNcaaTournament;
    @Column(name = "espn_id", unique = true)
    private String espnId;
    @Column(name = "scrape_src_id")
    private Long scrapeSrcId;
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    public Game() {
    }

    public Game(Long id, LocalDate date, Season season, Team homeTeam, Team awayTeam, Integer homeScore, Integer awayScore, Integer numPeriods, Boolean isNeutral_site, String location, Double spread, Double overUnder, Boolean isConfTournament, Boolean isNcaaTournament, String espnId, Long scrapeSrcId, LocalDateTime publishedAt) {
        this.id = id;
        this.date = date;
        this.season = season;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.numPeriods = numPeriods;
        this.isNeutral_site = isNeutral_site;
        this.location = location;
        this.spread = spread;
        this.overUnder = overUnder;
        this.isConfTournament = isConfTournament;
        this.isNcaaTournament = isNcaaTournament;
        this.espnId = espnId;
        this.scrapeSrcId = scrapeSrcId;
        this.publishedAt = publishedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Integer getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
    }

    public Integer getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
    }

    public Integer getNumPeriods() {
        return numPeriods;
    }

    public void setNumPeriods(Integer numPeriods) {
        this.numPeriods = numPeriods;
    }

    public Boolean getNeutral_site() {
        return isNeutral_site;
    }

    public void setNeutral_site(Boolean neutral_site) {
        isNeutral_site = neutral_site;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getSpread() {
        return spread;
    }

    public void setSpread(Double spread) {
        this.spread = spread;
    }

    public Double getOverUnder() {
        return overUnder;
    }

    public void setOverUnder(Double overUnder) {
        this.overUnder = overUnder;
    }

    public Boolean getConfTournament() {
        return isConfTournament;
    }

    public void setConfTournament(Boolean confTournament) {
        isConfTournament = confTournament;
    }

    public Boolean getNcaaTournament() {
        return isNcaaTournament;
    }

    public void setNcaaTournament(Boolean ncaaTournament) {
        isNcaaTournament = ncaaTournament;
    }

    public String getEspnId() {
        return espnId;
    }

    public void setEspnId(String espnId) {
        this.espnId = espnId;
    }

    public Long getScrapeSrcId() {
        return scrapeSrcId;
    }

    public void setScrapeSrcId(Long scrapeSrcId) {
        this.scrapeSrcId = scrapeSrcId;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
}
