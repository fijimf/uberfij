package com.fijimf.deepfij.analytics;

import com.fijimf.deepfij.db.model.schedule.Game;
import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatistic;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatisticSummary;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.stream.Collectors;

public interface StatisticCalculator {
     static DailyStats calculate(Season season) {
        return null;
    }

    static LocalDate getStartDate(Map<LocalDate, List<Game>> gamesByDate, Season season) {
        return gamesByDate.keySet().stream().min(Comparator.naturalOrder()).orElse(season.defaultStartDate());
    }

    static LocalDate getEndDate(Map<LocalDate, List<Game>> gamesByDate, Season season) {
        return gamesByDate.keySet().stream().max(Comparator.naturalOrder()).orElse(season.defaultEndDate());
    }

    static Map<LocalDate, List<Game>> getGamesByDate(Season season) {
        return season.getGames().stream().filter(Game::isComplete).collect(Collectors.groupingBy(Game::getDate));
    }
}
