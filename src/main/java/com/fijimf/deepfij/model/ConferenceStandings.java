package com.fijimf.deepfij.model;

import com.fijimf.deepfij.db.model.schedule.Conference;
import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.schedule.Team;

import java.util.List;

public class ConferenceStandings {

    private final Conference conference;
    private final List<StandingsItem> standings;

    public ConferenceStandings(Conference conference, List<StandingsItem> standings) {
        this.conference = conference;
        this.standings = standings;
    }

    public static ConferenceStandings fromSeason(Season s, Conference c) {
        List<Team> teams = s.getTeams(c);
        List<StandingsItem> standings = teams.stream().map(t -> {
            Record conf = Record.createRecord("Conference", t, s.getGames().stream().filter(s::isRegularConferenceGame).toList());
            Record nonconf = Record.createRecord("NonConference", t, s.getGames().stream().filter(g -> !s.isRegularConferenceGame(g)).toList());
            return new StandingsItem(t, conf, nonconf);
        }).sorted().toList();
        return new ConferenceStandings(c, standings);
    }

    public Conference getConference() {
        return conference;
    }

    public List<StandingsItem> getStandings() {
        return standings;
    }
    public static final class StandingsItem implements Comparable<StandingsItem> {
        private final Team team;
        private final Record conferenceRecord;
        private final Record nonConferenceRecord;


        public StandingsItem(Team team, Record conferenceRecord, Record nonConferenceRecord) {
            this.team = team;
            this.conferenceRecord = conferenceRecord;
            this.nonConferenceRecord = nonConferenceRecord;
        }

        public Team getTeam() {
            return team;
        }

        public Record getConferenceRecord() {
            return conferenceRecord;
        }

        public Record getNonConferenceRecord() {
            return nonConferenceRecord;
        }

        @Override
        public int compareTo(ConferenceStandings.StandingsItem o) {
            int n = (conferenceRecord.getWins() - conferenceRecord.getLosses()) - (o.conferenceRecord.getWins() - o.conferenceRecord.getLosses());
            if (n == 0) {
                n = conferenceRecord.getWins() - o.conferenceRecord.getWins();
                if (n == 0) {
                    n = o.getTeam().getName().compareTo(getTeam().getName());
                }
            }
            return -n;
        }
    }
}
