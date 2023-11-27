package com.fijimf.deepfij.model;

import com.fijimf.deepfij.db.model.schedule.Conference;
import com.fijimf.deepfij.db.model.schedule.Game;
import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.schedule.Team;

import java.util.*;
import java.util.stream.Collectors;

public class TeamPage {
    private final Season currentSeason;
    private final Team team;
    private final List<Season> seasons;

    private final List<String> messages = new ArrayList<>();
    private final List<SeasonalRecord> seasonalRecords;

    public TeamPage(int currentYear, Team team, List<Season> seasons) {
        if (team == null)
            throw new IllegalArgumentException("Team cannot be null");
        if (seasons == null || seasons.isEmpty())
            throw new IllegalArgumentException("Seasons cannot be null or empty");
        this.team = team;
        this.seasons = seasons;
        this.currentSeason = seasons.stream().filter(s -> s.getSeason() == currentYear).findFirst()
                .orElseGet(() -> {
                    messages.add("Could not find season " + currentYear);
                    return seasons.stream().max(Comparator.comparing(Season::getSeason)).orElseThrow();
                });
        this.seasonalRecords = createSeasonalRecords();
    }

    public TeamPage(Team team, List<Season> seasons) {
        if (team == null)
            throw new IllegalArgumentException("Team cannot be null");
        if (seasons == null || seasons.isEmpty())
            throw new IllegalArgumentException("Seasons cannot be null or empty");
        this.team = team;
        this.seasons = seasons;
        this.currentSeason = seasons.stream().max(Comparator.comparing(Season::getSeason)).get();
        this.seasonalRecords = createSeasonalRecords();
    }

    public Team getTeam() {
        return team;
    }

    public String getTitle() {
        return team.getLongName();
    }

    public String getLogoUrl() {
        return team.getLogoUrl();
    }

    public Conference getCurrentConference() {
        return currentSeason.getConference(team);
    }

    public int getCurrentYear() {
        return currentSeason.getSeason();
    }

    public List<SeasonalRecord> getSeasonalRecords() {
        return seasonalRecords;
    }

    public Record getCurrentRecord() {
        return getSeasonalRecords().stream()
                .filter(s -> s.getSeason() == getCurrentYear())
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No seasonal record found for the current year"))
                .getOverall();
    }

    public Record getCurrentConfRecord() {
        return getSeasonalRecords().stream()
                .filter(s -> s.getSeason() == getCurrentYear())
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No seasonal record found for the current year"))
                .getConference();
    }

    public String getTeamColor() {
        return team.getColor();
    }

    public List<ScheduleItem> getScheduleItems() {
        return currentSeason
                .getGames()
                .stream()
                .filter(g -> g.hasTeam(team))
                .sorted(Comparator.comparing(Game::getDate))
                .map(g -> new ScheduleItem(team, g))
                .collect(Collectors.toList());
    }

    private List<SeasonalRecord> createSeasonalRecords() {
        return seasons.stream().filter(s -> s.getConference(team) != null).map(
                s -> {
                    Set<Game> games = s.getGames();
                    return new SeasonalRecord(s.getSeason(),
                            Record.createRecord("Overall", team, games.stream().toList()),
                            Record.createRecord(s.getConference(team).getAltName(), team,
                                    s.getConferenceGames().stream().toList()),
                            Record.createRecord("Home", team,
                                    games.stream().filter(g -> g.isHomeTeam(team, false)).toList()),
                            Record.createRecord("Away", team,
                                    games.stream().filter(g -> g.isAwayTeam(team, false)).toList()),
                            Record.createRecord("Neutral", team, games.stream().filter(Game::isNeutralSite).toList()));
                }).sorted(Comparator.comparing(SeasonalRecord::getSeason).reversed()).toList();

    }

    public static class SeasonalRecord {
        private final int season;
        private final Record overall;
        private final Record conference;
        private final Record home;
        private final Record away;
        private final Record neutral;

        public SeasonalRecord(int season, Record overall, Record conference, Record home, Record away, Record neutral) {
            this.season = season;
            this.overall = overall;
            this.conference = conference;
            this.home = home;
            this.away = away;
            this.neutral = neutral;
        }

        public int getSeason() {
            return season;
        }

        public Record getOverall() {
            return overall;
        }

        public Record getConference() {
            return conference;
        }

        public Record getHome() {
            return home;
        }

        public Record getAway() {
            return away;
        }

        public Record getNeutral() {
            return neutral;
        }
    }
}
