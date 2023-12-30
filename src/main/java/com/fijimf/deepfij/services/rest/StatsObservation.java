package com.fijimf.deepfij.services.rest;

import java.time.LocalDate;

public record StatsObservation(LocalDate date, Double value, Double min, Double q1, Double ned, Double q3, Double max,
                               Double mean, Double stdDev, Double zScore, Double stdScore) {
}
