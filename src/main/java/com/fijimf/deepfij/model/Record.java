package com.fijimf.deepfij.model;

import com.fijimf.deepfij.db.model.schedule.Game;
import com.fijimf.deepfij.db.model.schedule.Team;

import java.util.List;

public class Record {
    private final String label;
    private final Integer wins;

    private final Integer losses;


    public Record(String label, Integer wins, Integer losses) {
        this.label = label;
        this.wins = wins;
        this.losses = losses;
    }

    public String getLabel() {
        return label;
    }

    public Integer getWins() {
        return wins;
    }

    public Integer getLosses() {
        return losses;
    }

    public static Record createRecord(String label, Team team, List<Game> games) {
        Integer wins = Math.toIntExact(games.stream().filter(g -> g.isWinner(team)).count());
        Integer losses = Math.toIntExact(games.stream().filter(g -> g.isLoser(team)).count());
        return new Record(label, wins, losses);
    }

    public String display() {
        return String.format("%d - %d", wins, losses);
    }
}
