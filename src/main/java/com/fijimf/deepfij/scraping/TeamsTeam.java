package com.fijimf.deepfij.scraping;

import com.fijimf.deepfij.db.model.schedule.Team;

public class TeamsTeam {
    private TeamTeam team;

    public TeamsTeam() {
    }

    public TeamsTeam(TeamTeam team) {
        this.team = team;
    }

    public TeamTeam getTeam() {
        return team;
    }

    public void setTeam(TeamTeam team) {
        this.team = team;
    }

    public Team value() {
        return team.value();
    }
}
