package com.fijimf.deepfij.model;

import com.fijimf.deepfij.db.model.schedule.Game;
import com.fijimf.deepfij.db.model.schedule.Team;

import java.time.format.DateTimeFormatter;

public class ScheduleItem {
    private final Team subject;
    private final Game game;

    public ScheduleItem(Team subject, Game game) {
        this.subject = subject;
        this.game = game;
    }

    public String getDate() {
        return game.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public boolean isComplete() {
        return (game.getHomeTeam() != null && game.getAwayScore() != null);
    }

    public String getWL() {
        if (game.isWinner(subject)) {
            return "W";
        } else if (game.isLoser(subject)) {
            return "L";
        } else {
            return "";
        }
    }

    public String getAtVs() {
        if (game.isAwayTeam(subject, true)) {
            return "@";
        } else {
            return "vs.";
        }
    }

    public String getTeamName() {
        return game.getOpponent(subject).getName();
    }

    public String getTeamKey() {
        return game.getOpponent(subject).getKey();
    }

    public String getOvertimes() {
        if (isComplete() && game.getNumPeriods() == 3) {
            return "OT";
        } else if (isComplete() && game.getNumPeriods() > 3) {
            return (game.getNumPeriods() - 2) + "OT";
        } else {
            return "";
        }
    }

    public String getResult() {
        if (isComplete()) {
            if (game.isAwayTeam(subject, true)) {
                return getWL() + " " + game.getAwayScore() + "-" + game.getHomeScore() + " " + getOvertimes();
            } else {
                return getWL() + " " + game.getAwayScore() + "-" + game.getHomeScore() + " " + getOvertimes();
            }
        } else {
            return "";
        }
    }
}
