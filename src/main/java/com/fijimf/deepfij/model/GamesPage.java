package com.fijimf.deepfij.model;


import java.time.LocalDate;
import java.util.List;

public record GamesPage(LocalDate date, LocalDate prev, LocalDate next, int season, LocalDate asOf,
                        List<GameLine> games) {
}
