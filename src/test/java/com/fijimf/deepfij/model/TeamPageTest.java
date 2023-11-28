package com.fijimf.deepfij.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TeamPageTest {

    @Test
    public void testGetSeasonalRecords() {
        // Create a TeamPage object
        TeamPage teamPage = new TeamPage();

        // Create a list of SeasonalRecord objects
        List<SeasonalRecord> seasonalRecords = new ArrayList<>();
        seasonalRecords.add(new SeasonalRecord());
        seasonalRecords.add(new SeasonalRecord());
        seasonalRecords.add(new SeasonalRecord());

        // Set the seasonalRecords list in the TeamPage object
        teamPage.setSeasonalRecords(seasonalRecords);

        // Call the getSeasonalRecords() method and assert that it returns the same list
        assertEquals(seasonalRecords, teamPage.getSeasonalRecords());
    }
}