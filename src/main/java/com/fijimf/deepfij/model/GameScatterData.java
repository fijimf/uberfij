package com.fijimf.deepfij.model;

import java.util.List;

public record GameScatterData(String shortName, String color, List<TeamGame> data) {

    public record TeamGame(int season, int year, int month, int day, int score, int oppScore, String oppName,
                           String oppLogoUrl) {
    }

}
