package com.fijimf.deepfij.db.model.schedule;

import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "games") // FIXME
public class Game {
    private static final Logger logger = LoggerFactory.getLogger(Game.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;

    @Column(name = "scoreboard_key")
    private LocalDate scoreboardKey;
    @ManyToOne
    @JoinColumn(name = "season_id")
    private Season season;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "home_team_id")
    private Team homeTeam;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "away_team_id")
    private Team awayTeam;
    @Column(name = "home_score")
    private Integer homeScore;
    @Column(name = "away_score")
    private Integer awayScore;
    @Column(name = "num_periods")
    private Integer numPeriods;
    @Column(name = "is_neutral_site")
    private Boolean isNeutralSite;
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

    public Game(Long id, LocalDate date, LocalDate scoreboardKey, Season season, Team homeTeam, Team awayTeam,
                Integer homeScore, Integer awayScore, Integer numPeriods, Boolean isNeutralSite, String location,
                Double spread, Double overUnder, Boolean isConfTournament, Boolean isNcaaTournament, String espnId,
                Long scrapeSrcId, LocalDateTime publishedAt) {
        this.id = id;
        this.date = date;
        this.scoreboardKey = scoreboardKey;
        this.season = season;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.numPeriods = numPeriods;
        this.isNeutralSite = isNeutralSite;
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

    public LocalDate getScoreboardKey() {
        return scoreboardKey;
    }

    public void setScoreboardKey(LocalDate scoreboardKey) {
        this.scoreboardKey = scoreboardKey;
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

    public Boolean getNeutralSite() {
        return isNeutralSite;
    }

    public void setNeutralSite(Boolean neutral_site) {
        isNeutralSite = neutral_site;
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

    public LocalDate scoreboardKey() {
        return date;
    }

    public boolean isEffectivelyEqual(Game game) {
        return Objects.equals(getDate(), game.getDate())
                && Objects.equals(getSeason(), game.getSeason())
                && Objects.equals(getHomeTeam(), game.getHomeTeam())
                && Objects.equals(getAwayTeam(), game.getAwayTeam())
                && Objects.equals(getHomeScore(), game.getHomeScore())
                && Objects.equals(getAwayScore(), game.getAwayScore())
                && Objects.equals(getNumPeriods(), game.getNumPeriods())
                && Objects.equals(isNeutralSite, game.isNeutralSite)
                && Objects.equals(getLocation(), game.getLocation())
                && Objects.equals(getSpread(), game.getSpread())
                && Objects.equals(getOverUnder(), game.getOverUnder())
                && Objects.equals(isConfTournament, game.isConfTournament)
                && Objects.equals(isNcaaTournament, game.isNcaaTournament);
    }

    public Optional<Game> createUpdate(Game target) {
        if (isEffectivelyEqual(target)) {
            return Optional.empty();
        } else {
            logger.info("OLD|" + target);
            logger.info("NEW|" + this);
            target.setDate(date);
            target.setSeason(season);
            target.setHomeTeam(homeTeam);
            target.setAwayTeam(awayTeam);
            target.setHomeScore(homeScore);
            target.setAwayScore(awayScore);
            target.setNumPeriods(numPeriods);
            target.setNeutralSite(isNeutralSite);
            target.setLocation(location);
            target.setSpread(spread);
            target.setOverUnder(overUnder);
            target.setConfTournament(isConfTournament);
            target.setNcaaTournament(isNcaaTournament);
            return Optional.of(target);
        }
    }

    public boolean isWinner(Team team) {
        if (team == null || homeScore == null || awayScore == null) {
            return false;
        } else {
            return ((homeScore < awayScore && team.getId() == awayTeam.getId()) ||
                    (homeScore > awayScore && team.getId() == homeTeam.getId()));
        }
    }

    public boolean isLoser(Team team) {
        if (team == null || homeScore == null || awayScore == null) {
            return false;
        } else {
            return ((homeScore > awayScore && team.getId() == awayTeam.getId()) ||
                    (homeScore < awayScore && team.getId() == homeTeam.getId()));
        }
    }

    public boolean isHomeTeam(Team team, boolean trueHome) {
        if (trueHome) {
            return team.getId() == homeTeam.getId();
        } else {
            return (team.getId() == homeTeam.getId()) && isNeutralSite != Boolean.TRUE;
        }
    }

    public boolean isAwayTeam(Team team, boolean trueAway) {
        if (trueAway) {
            return team.getId() == awayTeam.getId();
        } else {
            return (team.getId() == awayTeam.getId()) && isNeutralSite != Boolean.TRUE;
        }
    }

    public boolean isNeutralSite() {
        return isNeutralSite == Boolean.TRUE; // null is treated as false
    }

    public boolean hasTeam(Team team) {
        return homeTeam.getId() == team.getId() || awayTeam.getId() == team.getId();
    }

    public Team getOpponent(Team t) {
        if (t.getId() == homeTeam.getId()) {
            return awayTeam;
        } else if (t.getId() == awayTeam.getId()) {
            return homeTeam;
        } else {
            throw new RuntimeException();
        }
    }

    public Integer getScore(Team t) {
        if (t.getId() == homeTeam.getId()) {
            return homeScore;
        } else if (t.getId() == awayTeam.getId()) {
            return awayScore;
        } else {
            throw new RuntimeException();
        }
    }

    public Integer getOppScore(Team t) {
        if (t.getId() == homeTeam.getId()) {
            return awayScore;
        } else if (t.getId() == awayTeam.getId()) {
            return homeScore;
        } else {
            throw new RuntimeException();
        }
    }

    public Integer getMargin(Team t) {
        return getScore(t) - getOppScore(t);
    }

    public boolean isComplete() {
        return homeScore != null && awayScore != null;
    }

    @Override
    public String toString() {
        return "%6d|%s|%s|%4d|%12s|%12s|%3d|%3d|%1d|%s|%12s|%5.1f|%.1f|%s|%s|%14s|%5d|%s}"
                .formatted(id,
                        date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                        scoreboardKey.format(DateTimeFormatter.ISO_LOCAL_DATE),
                        season.getSeason(),
                        StringUtils.truncate(homeTeam.getName(), 12),
                        StringUtils.truncate(awayTeam.getName(), 12),
                        homeScore,
                        awayScore,
                        numPeriods,
                        isNeutralSite == null ? " " : (isNeutralSite ? "Y" : "N"),
                        StringUtils.truncate(location, 12),
                        spread,
                        overUnder,
                        isNeutralSite == null ? " " : (isConfTournament ? "Y" : "N"),
                        isNeutralSite == null ? " " : (isNcaaTournament ? "Y" : "N"),
                        StringUtils.truncate(espnId, 14),
                        scrapeSrcId,
                        publishedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    public String getLine() {
        if (spread == null) {
            return null;
        } else if (spread == 0) {
            return "Pick";
        } else if (spread < 0) {
            return awayTeam.getAltName1() + " +" + (-spread);
        } else {
            return homeTeam.getAltName1() + " +" + spread;
        }
    }

    public String getLineResult() {
        if (spread == null || homeScore == null || awayScore == null) {
            return null;
        } else {
            if (homeScore + spread > awayScore) {
                return homeTeam.getAltName1();
            } else {
                return awayTeam.getAltName1();
            }
        }
    }

    public String getOverUnderResult() {
        if (overUnder == null || homeScore == null || awayScore == null) {
            return null;
        } else {
            if (homeScore + awayScore == overUnder) {
                return "PUSH";
            } else if (homeScore + awayScore > overUnder) {
                return "OVER";
            } else {
                return "UNDER";
            }
        }
    }
}
