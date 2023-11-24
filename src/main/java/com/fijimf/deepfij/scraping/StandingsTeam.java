package com.fijimf.deepfij.scraping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fijimf.deepfij.db.model.schedule.Team;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StandingsTeam {
    private String id;
    private String shortDisplayName;
    private String location;
    private String name;
    private String abbreviation;
    private String displayName;

    private TeamLogo[] logos;

    public StandingsTeam() {
    }

    public StandingsTeam(String id, String shortDisplayName, String location, String name, String abbreviation, String displayName, TeamLogo[] logos) {
        this.id = id;
        this.shortDisplayName = shortDisplayName;
        this.location = location;
        this.name = name;
        this.abbreviation = abbreviation;
        this.displayName = displayName;
        this.logos = logos;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public TeamLogo[] getLogos() {
        return logos;
    }

    public void setLogos(TeamLogo[] logos) {
        this.logos = logos;
    }

    public Team value() {
        return new Team(0L,
                displayName.toLowerCase().replace(' ', '-').replaceAll("[^a-z\\-]", ""),
                shortDisplayName,
                name,
                displayName,
                abbreviation,
                name,
                "D0D0D0",
                "D0D0D0",
                logos[0].getHref(),
                id,
                0L,
                LocalDateTime.now()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandingsTeam that = (StandingsTeam) o;
        return Objects.equals(id, that.id) && Objects.equals(shortDisplayName, that.shortDisplayName) && Objects.equals(location, that.location) && Objects.equals(name, that.name) && Objects.equals(abbreviation, that.abbreviation) && Objects.equals(displayName, that.displayName) && Arrays.equals(logos, that.logos);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, shortDisplayName, location, name, abbreviation, displayName);
        result = 31 * result + Arrays.hashCode(logos);
        return result;
    }
}
