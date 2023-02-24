package com.fijimf.deepfij.scraping.util;

public class TeamNotFoundException extends RuntimeException {
    public TeamNotFoundException(String id) {
        super("Team with ESPN id '"+id+"'not found");
    }
}
