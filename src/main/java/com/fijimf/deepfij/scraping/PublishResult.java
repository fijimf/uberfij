package com.fijimf.deepfij.scraping;

import java.time.LocalDate;

public class PublishResult {
    private final LocalDate key;
    private final int existingGames = 0;
    private final int gamesInserted;
    private final int gamesUpdated;
    private final int gamesDeleted;
    private final String message;

    public PublishResult(LocalDate key, int gamesInserted, int gamesUpdated, int gamesDeleted, String message) {
        this.key = key;
        this.gamesInserted = gamesInserted;
        this.gamesUpdated = gamesUpdated;
        this.gamesDeleted = gamesDeleted;
        this.message = message;
    }

    public LocalDate getKey() {
        return key;
    }

    public int getGamesInserted() {
        return gamesInserted;
    }

    public int getGamesUpdated() {
        return gamesUpdated;
    }

    public int getGamesDeleted() {
        return gamesDeleted;
    }

    public String getMessage() {
        return message;
    }

}
