package com.fijimf.deepfij.scraping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StandingsLine {
    private StandingsTeam team;

    public StandingsLine() {
    }

    public StandingsLine(StandingsTeam team) {
        this.team = team;
    }

    public StandingsTeam getTeam() {
        return team;
    }

    public void setTeam(StandingsTeam team) {
        this.team = team;
    }
}
