package com.fijimf.deepfij.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record GameSnapshot(Long id, String slug, LocalDate date, TeamSnapshot homeSnapshot, Integer homeScore,
                           TeamSnapshot awaySnapshot, Integer awayScore, Double spread, Double overUnder) {

    @Override
    @JsonFormat(pattern = "yyyy-MM-dd")
    public LocalDate date() {
        return date;
    }
}
