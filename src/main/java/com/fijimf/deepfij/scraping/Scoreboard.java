package com.fijimf.deepfij.scraping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Scoreboard {
    private  List<ScoreboardSport> sports;

    public Scoreboard() {
    }

    public Scoreboard(List<ScoreboardSport> sports) {
        this.sports = sports;
    }

    public List<ScoreboardSport> getSports() {
        return sports;
    }

    public int numberOfGames() {
        if (sports == null || sports.isEmpty()) {
            return 0;
        } else {
            return sports.get(0).numberOfGames();
        }
    }

}
