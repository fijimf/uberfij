package com.fijimf.deepfij.db.model.schedule;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "season")
public class Season {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private int season;

    @OneToMany(mappedBy = "id")
    private Set<ConferenceMap> conferenceMaps= new HashSet<>();
    @OneToMany(mappedBy = "id")
    private Set<Game> games= new HashSet<>();
    public Season() {
    }

    public Season(long id, int season) {
        this.id = id;
        this.season = season;
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
    }

    public Set<Game> getGames() {
        return games;
    }

    public void setGames(Set<Game> games) {
        this.games = games;
    }
}
