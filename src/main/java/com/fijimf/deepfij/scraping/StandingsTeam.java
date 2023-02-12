package com.fijimf.deepfij.scraping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StandingsTeam {
    private String id;
    private String shortDisplayName;

    public StandingsTeam() {
    }

    public StandingsTeam(String id, String shortDisplayName) {
        this.id = id;
        this.shortDisplayName = shortDisplayName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShortDisplayName() {
        return shortDisplayName;
    }

    public void setShortDisplayName(String shortDisplayName) {
        this.shortDisplayName = shortDisplayName;
    }
}
