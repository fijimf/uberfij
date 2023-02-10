package com.fijimf.deepfij.scraping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fijimf.deepfij.db.model.schedule.Team;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Teams {
    TeamsSport[] sports;

    public Teams() {
    }

    public Teams(TeamsSport[] sports) {
        this.sports = sports;
    }

    public TeamsSport[] getSports() {
        return sports;
    }

    public void setSports(TeamsSport[] sports) {
        this.sports = sports;
    }

    public List<Team> values() {
        return Arrays.stream(sports).findFirst().map(TeamsSport::values).orElse(Collections.emptyList());
    }
}
