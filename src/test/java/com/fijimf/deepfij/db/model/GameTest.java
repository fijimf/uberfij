package com.fijimf.deepfij.db.model;

import org.junit.jupiter.api.Test;

import com.fijimf.deepfij.db.model.schedule.Game;
import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.schedule.Team;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class GameTest {

    @Test
    public void testIsEffectivelyEqual_SameGame_ReturnsTrue() {
        // Arrange
        Game game1 = new Game();
        game1.setDate(LocalDate.of(2022, 1, 1));
        game1.setSeason(new Season(1L, 2022));
        game1.setHomeTeam(new Team(1L, "Georgetown", "Hoyas", "Big East", "http://www.georgetown.edu",
                "http://www.georgetown.edu", "http://www.georgetown.edu", null, null, null, null, 0, null));
        game1.setAwayTeam(new Team(2L, "Villanova", "Wildcats", "Big East", "http://www.villanova.edu",
                "http://www.villanova.edu", "http://www.villanova.edu", null, null, null, null, 0, null));
        game1.setHomeScore(10);
        game1.setAwayScore(5);
        game1.setNumPeriods(4);
        game1.setNeutralSite(true);
        game1.setLocation("Stadium");
        game1.setSpread(2.5);
        game1.setOverUnder(40.5);
        game1.setConfTournament(true);
        game1.setNcaaTournament(false);

        // Act
        boolean result = game1.isEffectivelyEqual(game1);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testIsEffectivelyEqual_SameDetails_ReturnsTrue() {
        Season season = new Season(1L, 2022);
        Team v = new Team(2L, "Villanova", "Wildcats", "Big East", "http://www.villanova.edu",
                "http://www.villanova.edu", "http://www.villanova.edu", null, null, null, null, 0, null);
        Team g = new Team(1L, "Georgetown", "Hoyas", "Big East", "http://www.georgetown.edu",
                "http://www.georgetown.edu", "http://www.georgetown.edu", null, null, null, null, 0, null);

        // Arrange
        Game game1 = new Game();
        game1.setDate(LocalDate.of(2022, 1, 1));
        game1.setSeason(season);
        game1.setHomeTeam(g);
        game1.setAwayTeam(v);
        game1.setHomeScore(10);
        game1.setAwayScore(5);
        game1.setNumPeriods(4);
        game1.setNeutralSite(true);
        game1.setLocation("Stadium");
        game1.setSpread(2.5);
        game1.setOverUnder(40.5);
        game1.setConfTournament(true);
        game1.setNcaaTournament(false);

        Game game2 = new Game();
        game2.setDate(LocalDate.of(2022, 1, 1));

        game2.setSeason(season);
        game2.setHomeTeam(g);
        game2.setAwayTeam(v);
        game2.setHomeScore(10);
        game2.setAwayScore(5);
        game2.setNumPeriods(4);
        game2.setNeutralSite(true);
        game2.setLocation("Stadium");
        game2.setSpread(2.5);
        game2.setOverUnder(40.5);
        game2.setConfTournament(true);
        game2.setNcaaTournament(false);

        // Act
        boolean result = game1.isEffectivelyEqual(game2);

        // Assert
        assertTrue(result);
    }
}