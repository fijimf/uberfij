package com.fijimf.deepfij.analytics;

import com.fijimf.deepfij.db.model.schedule.Game;
import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.schedule.Team;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatistic;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatisticSummary;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WonLost {

    public static final String WINS = "Wins";
    public static final String LOSSES = "Losses";
    public static final String WIN_PCT = "WinPct";
    public static final String WIN_STREAK = "WinStreak";
    public static final String LOSS_STREAK = "LossStreak";

    public static Map<LocalDate, Map<DailyTeamStatisticSummary, List<DailyTeamStatistic>>> calculate(Season season) {
        Map<LocalDate, List<Game>> ganesByDate = season.getGames().stream().filter(Game::isComplete).collect(Collectors.groupingBy(Game::getDate));

        return ganesByDate.keySet().stream().sorted().map(d -> {
            return Pair.of(d, calculateTeamStatisticsByDate(season, d, ganesByDate));
        }).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    private static Map<DailyTeamStatisticSummary, List<DailyTeamStatistic>> calculateTeamStatisticsByDate(Season season, LocalDate d, Map<LocalDate, List<Game>> ganesByDate) {
        Map<Team, Map<String, Double>> runningTotal = new HashMap<>();
        List<Game> gamesOnDate = ganesByDate.get(d);
        gamesOnDate.forEach(g -> {
            accumulate(g, runningTotal, g.getHomeTeam());
            accumulate(g, runningTotal, g.getAwayTeam());
        });

        List<DailyTeamStatisticSummary> summaries =
                Stream.of(WINS, LOSSES, WIN_PCT, WIN_STREAK, LOSS_STREAK).map(statName -> {
                    DescriptiveStatistics statistics = new DescriptiveStatistics();
                    for (Team team : runningTotal.keySet()) {
                        statistics.addValue(runningTotal.get(team).get(statName));
                    }
                    return new DailyTeamStatisticSummary(0L, season, d, statName, statistics.getMean(), (int) statistics.getN(), statistics.getMin(), statistics.getMax(), statistics.getPercentile(50), statistics.getPercentile(25), statistics.getPercentile(75), statistics.getStandardDeviation(), statistics.getKurtosis(), statistics.getSkewness());
                }).toList();

        return summaries.stream().map(summary -> {
            List<DailyTeamStatistic> dailyTeamStatistics = runningTotal.entrySet().stream().map(entry -> {
                Team team = entry.getKey();
                Map<String, Double> values = entry.getValue();
                if (values.containsKey(summary.getStatistic())){
                    Double value = values.get(summary.getStatistic());
                    return new DailyTeamStatistic(0L, summary, team, value);
                } else {
                    return null;
                }
            }).filter(Objects::nonNull).toList();
            Pair<DailyTeamStatisticSummary, List<DailyTeamStatistic>> dailyTeamStatisticSummaryListPair = Pair.of(summary, dailyTeamStatistics);
            return dailyTeamStatisticSummaryListPair;
        }).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    private static void accumulate(Game g, Map<Team, Map<String, Double>> runningTotal, Team team) {
        Map<String, Double> teamStats = runningTotal.getOrDefault(team, new HashMap<>());
        if (g.isWinner(team)) {
            teamStats.put(WINS, teamStats.getOrDefault(WINS, 0.0) + 1.0);
            teamStats.put(LOSSES, teamStats.getOrDefault(LOSSES, 0.0) + 0.0);
            teamStats.put(WIN_PCT, teamStats.get(WINS) / (teamStats.get(WINS) + teamStats.get(LOSSES)));
            teamStats.put(WIN_STREAK, teamStats.getOrDefault(WIN_STREAK, 0.0) + 1.0);
            teamStats.put(LOSS_STREAK, 0.0);
        } else {
            teamStats.put(WINS, teamStats.getOrDefault(WINS, 0.0) + 0.0);
            teamStats.put(LOSSES, teamStats.getOrDefault(LOSSES, 0.0) + 1.0);
            teamStats.put(WIN_PCT, teamStats.get(WINS) / (teamStats.get(WINS) + teamStats.get(LOSSES)));
            teamStats.put(WIN_STREAK, 0.0);
            teamStats.put(LOSS_STREAK, teamStats.getOrDefault(LOSS_STREAK, 0.0) + 1.0);
        }
    }
}
