package com.fijimf.deepfij.db.model.schedule;

import com.fijimf.deepfij.scraping.SeasonManager;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "season")
public class Season {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private int season;

    @OneToMany()
    @JoinColumn(name = "season_id")
    private Set<ConferenceMap> conferenceMaps = new HashSet<>();
    @OneToMany(mappedBy = "id")
    private Set<Game> games = new HashSet<>();


    @Transient
    private Map<Team, Conference> teamToConference;

    @Transient
    private Map<Conference, List<Team>> conferenceToTeams;

    public Season() {
        teamToConference = Collections.emptyMap();
        conferenceToTeams = Collections.emptyMap();
    }

    public Season(long id, int season) {
        this.id = id;
        this.season = season;
        teamToConference = Collections.emptyMap();
        conferenceToTeams = Collections.emptyMap();
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

    public Set<ConferenceMap> getConferenceMaps() {
        return conferenceMaps;
    }

    public void setConferenceMaps(Set<ConferenceMap> conferenceMaps) {
        this.conferenceMaps = conferenceMaps;
        synchConvenienceMaps();
    }

    @PostLoad
    private void synchConvenienceMaps() {
        this.teamToConference = conferenceMaps.stream().collect(Collectors.toMap(ConferenceMap::getTeam, ConferenceMap::getConference));
        this.conferenceToTeams = new HashMap<>();
        conferenceMaps.forEach(cm -> {
            Conference key = cm.getConference();
            if (!conferenceToTeams.containsKey(key)) {
                conferenceToTeams.put(key, new ArrayList<>());
            }
            List<Team> teams = conferenceToTeams.get(key);
            teams.add(cm.getTeam());
            conferenceToTeams.put(key, teams);
        });
        conferenceToTeams.forEach((k, v) -> v.sort(Comparator.comparing(Team::getName)));
    }

    public Set<Game> getGames() {
        return games;
    }

    public Set<Game> getDamesForKey(LocalDate key) {
        return games.stream().filter(g -> g.scoreboardKey().equals(key)).collect(Collectors.toSet());
    }

    public void setGames(Set<Game> games) {
        this.games = games;
    }

    public int numTeams() {
        return teamToConference.size();
    }

    public int numConferences() {
        return conferenceToTeams.size();
    }

    public List<Map.Entry<Conference, List<Team>>> conferenceList() {
        return conferenceToTeams.entrySet().stream().sorted(Comparator.comparing(e -> e.getKey().getName())).toList();
    }

    public List<LocalDate> gameDates() {
        return games.stream().map(Game::getDate).distinct().sorted().toList();
    }

    public LocalDate defaultStartDate() {
        return SeasonManager.defaultStartDate(season);
    }

    public LocalDate defaultEndDate() {
        return SeasonManager.defaultEndDate(season);
    }
}
