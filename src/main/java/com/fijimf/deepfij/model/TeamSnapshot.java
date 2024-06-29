package com.fijimf.deepfij.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record TeamSnapshot(SimpleTeam team, @JsonFormat(pattern = "yyyy-MM-dd") LocalDate asOf, Record record,
                           Record last5, Double pointsForAvg, Double pointsForStdDev, Double pointsForQ1,
                           Double pointsForQ3, Double pointsAgainstAvg, Double pointsAgainstStdDev,
                           Double pointsAgainstQ1, Double pointsAgainstQ3, Double pfpaCorr, Double c95majorAxis,
                           Double c95minorAxis, Double c95angle) {
    public TeamSnapshot(SimpleTeam team, LocalDate asOf, Record record, Record last5, Double pointsForAvg, Double pointsForStdDev, Double pointsForQ1, Double pointsForQ3, Double pointsAgainstAvg, Double pointsAgainstStdDev, Double pointsAgainstQ1, Double pointsAgainstQ3, Double pfpaCorr, Double c95majorAxis, Double c95minorAxis, Double c95angle) {
        this.team = team;
        this.asOf = asOf;
        this.record = record;
        this.last5 = last5;
        this.pointsForAvg = pointsForAvg;
        this.pointsForStdDev = pointsForStdDev;
        this.pointsForQ1 = pointsForQ1;
        this.pointsForQ3 = pointsForQ3;
        this.pointsAgainstAvg = pointsAgainstAvg;
        this.pointsAgainstStdDev = pointsAgainstStdDev;
        this.pointsAgainstQ1 = pointsAgainstQ1;
        this.pointsAgainstQ3 = pointsAgainstQ3;
        this.pfpaCorr = pfpaCorr;
        this.c95majorAxis = c95majorAxis;
        this.c95minorAxis = c95minorAxis;
        this.c95angle = c95angle;
    }

    @Override
    public LocalDate asOf() {
        return asOf;
    }
}

