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
    }

    public TeamPage(Team team, List<Season> seasons) {
        if (team == null)
            throw new IllegalArgumentException("Team cannot be null");
        if (seasons == null || seasons.isEmpty())
            throw new IllegalArgumentException("Seasons cannot be null or empty");
        this.team = team;
        this.seasons = seasons;
        this.currentSeason = seasons.stream().max(Comparator.comparing(Season::getSeason)).get();

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

    public Record getCurrentRecord() {
        return getSeasonalRecords().get(getCurrentYear()).get(0);
    }

    public Record getCurrentConfRecord() {
        return getSeasonalRecords().get(getCurrentYear()).get(1);
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

    public Map<Integer, List<Record>> getSeasonalRecords() {
        return seasons.stream().filter(s->s.getConference(team)!=null).collect(Collectors.toMap(
                Season::getSeason,
                s -> {
                    Set<Game> games = s.getGames();
                    Record overall = Record.createRecord("Overall", team, games.stream().toList());
                    Record conference = Record.createRecord(s.getConference(team).getAltName(), team, s.getConferenceGames().stream().toList());
                    Record home = Record.createRecord("Home", team, games.stream().filter(g -> g.isHomeTeam(team, false)).toList());
                    Record away = Record.createRecord("Away", team, games.stream().filter(g -> g.isAwayTeam(team, false)).toList());
                    Record neutral = Record.createRecord("Neutral", team, games.stream().filter(Game::isNeutralSite).toList());
                    return List.of(overall, conference, home, away, neutral);
                })
        );
    }
}
