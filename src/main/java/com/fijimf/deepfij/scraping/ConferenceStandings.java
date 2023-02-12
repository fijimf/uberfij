package com.fijimf.deepfij.scraping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConferenceStandings {
    private int season;
   private StandingsLine[] entries;

    public ConferenceStandings() {
    }

    public ConferenceStandings(int season, StandingsLine[] entries) {
        this.season = season;
       this.entries = entries;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public StandingsLine[] getEntries() {
        return entries;
    }

    public void setEntries(StandingsLine[] entries) {
        this.entries = entries;
    }
}
