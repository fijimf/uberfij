package com.fijimf.deepfij.model;

public record TeamLine(String name,
                       String key,
                       int wins,
                       int losses,
                       int streak,
                       int pointsFor,
                       int pointsAgainst) {
    public String performance() {
        double avgPF = (wins + losses > 0) ? (double) pointsFor / (wins + losses) : 0.0;
        double avgPA = (wins + losses > 0) ? (double) pointsAgainst / (wins + losses) : 0.0;
        if (streak == 0) {
            return "%d - %d, PF: %.1f, PA: %.1f".formatted(wins, losses, avgPF, avgPA);
        } else {
            return "%d - %d, %s%d, PF: %.1f, PA: %.1f".formatted(wins, losses, (streak > 0) ? "W" : "L", Math.abs(streak), avgPF, avgPA);
        }
    }

    public TeamLine update(int score, int oppScore) {
        if (score > oppScore) {
            return new TeamLine(name, key, wins + 1, losses, streak >= 0 ? streak + 1 : 1, pointsFor + score, pointsAgainst + oppScore);
        } else if (score < oppScore) {
            return new TeamLine(name, key, wins, losses + 1, streak <= 0 ? streak - 1 : -1, pointsFor + score, pointsAgainst + oppScore);
        } else {
            return this;
        }
    }
    public static TeamLine create(String name, String key) {
        return new TeamLine(name, key, 0,0,0,0,0);
    }
}