package com.fijimf.deepfij.scraping;

import java.util.List;

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

    public long numberOfGames() {
        if (sports == null || sports.isEmpty()) {
            return 0L;
        } else {
            return sports.get(0).numberOfGames();
        }
    }
}
