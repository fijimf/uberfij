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
    private final Conference conference;
    private final ConferenceStandings conferenceStandings;
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
        this.conference = currentSeason.getConference(team);
        this.conferenceStandings = ConferenceStandings.fromSeason(currentSeason, conference);
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
        this.conference = currentSeason.getConference(team);
        this.conferenceStandings = ConferenceStandings.fromSeason(currentSeason, conference);
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

    public Conference getConference() {
        return conference;
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

    public ConferenceStandings getConferenceStandings() {
        return conferenceStandings;
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
        return seasons.stream()
                .filter(s -> s.getConference(team) != null)
                .map(s -> SeasonalRecord.fromSeason(s, team, s.getSeason() == currentSeason.getSeason()))
                .sorted()
                .toList();
    }

    public String getSeasonQueryString(){
        if (getCurrentYear() == seasons.get(0).getSeason()) {
            return "";
        } else {
            return "?season=" + getCurrentYear();
        }
    }

}
