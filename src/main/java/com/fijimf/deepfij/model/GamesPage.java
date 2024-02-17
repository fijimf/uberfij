package com.fijimf.deepfij.model;

import com.fijimf.deepfij.db.model.schedule.Game;

import java.time.LocalDate;
import java.util.List;

public record GamesPage(LocalDate date, List<Game> games) {
}
