package com.fijimf.deepfij.scraping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Scoreboard {
    public static final Logger logger = LoggerFactory.getLogger(Scoreboard.class);
    private List<ScoreboardSport> sports;

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

    public List<ScoreboardGame> unpackGames() {
        if (sports == null || sports.isEmpty()) {
            return Collections.emptyList();
        } else {
            if (sports.size() > 1) logger.warn("Found multiple 'Sport's unpacking games in 'Scoreboard'");
            return sports.get(0).unpackGames();
        }
    }

}
