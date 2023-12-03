package com.fijimf.deepfij.model;

import com.fijimf.deepfij.db.model.schedule.Game;
import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.schedule.Team;

public class SeasonalRecord implements Comparable<SeasonalRecord> {
    private final int season;
    private final Record overall;
    private final Record conference;
    private final Record home;
    private final Record away;
    private final Record neutral;

    private final boolean focus;

    public SeasonalRecord(int season, Record overall, Record conference, Record home, Record away, Record neutral, boolean focus) {
        this.season = season;
        this.overall = overall;
        this.conference = conference;
        this.home = home;
        this.away = away;
        this.neutral = neutral;
        this.focus = focus;
    }

    public static SeasonalRecord fromSeason(Season s, Team t, boolean isFocus) {
        return new SeasonalRecord(s.getSeason(),
                Record.createRecord("Overall", t, s.getGames().stream().toList()),
                Record.createRecord("Conference", t, s.getConferenceGames().stream().toList()),
                Record.createRecord("Home", t, s.getGames().stream().filter(g -> g.isHomeTeam(t, false)).toList()),
                Record.createRecord("Away", t, s.getGames().stream().filter(g -> g.isHomeTeam(t, false)).toList()),
                Record.createRecord("Neutral", t, s.getGames().stream().filter(Game::isNeutralSite).toList()), isFocus);
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

    public boolean isFocus() {
        return focus;
    }

    @Override
    public int compareTo(SeasonalRecord other) {
        return -Integer.compare(season, other.season);
    }
}
