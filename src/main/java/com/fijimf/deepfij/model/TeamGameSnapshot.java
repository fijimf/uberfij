package com.fijimf.deepfij.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record TeamGameSnapshot(long id, SimpleTeam opp, int score, int oppScore, LocalDate date ) {
    @Override
    @JsonFormat(pattern = "yyyy-MM-dd")
    public LocalDate date() {
        return date;
    }
}
