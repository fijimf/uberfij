package com.fijimf.deepfij.analytics;

import com.fijimf.deepfij.db.model.schedule.Game;
import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.schedule.Team;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatistic;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatisticSummary;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WonLost {

    public static final String WINS = "Wins";
    public static final String LOSSES = "Losses";
    public static final String WIN_PCT = "WinPct";
    public static final String WIN_STREAK = "WinStreak";
    public static final String LOSS_STREAK = "LossStreak";

    public List<DailyTeamStatistic> calculate(Season season) {
        Map<LocalDate, List<Game>> ganesByDate = season.getGames().stream().filter(Game::isComplete).collect(Collectors.groupingBy(Game::getDate));
        Map<Team, Map<String, Double>> runningTotal = new HashMap<>();
        return ganesByDate.keySet().stream().sorted().flatMap(d -> {
            List<Game> gamesOnDate = ganesByDate.get(d);
            gamesOnDate.forEach(g -> {
                accumulate(g, runningTotal, g.getHomeTeam());
                accumulate(g, runningTotal, g.getAwayTeam());
            });

            Map<String, DescriptiveStatistics> meta = new HashMap<>();
            List.of(WINS, LOSSES, WIN_PCT, WIN_STREAK, LOSS_STREAK).forEach(s -> {
                meta.put(s, new DescriptiveStatistics());
                for (Team team : runningTotal.keySet()) {
                    meta.get(s).addValue(runningTotal.get(team).get(s));
                }
            });

            return runningTotal.keySet().stream().flatMap(t -> {
                Map<String, Double> teamStats = runningTotal.get(t);
                return Stream.of(WINS, LOSSES, WIN_PCT, WIN_STREAK, LOSS_STREAK).map(s -> {
                    Double value = teamStats.get(s);
                    DescriptiveStatistics statistics = meta.get(s);
                    DailyTeamStatisticSummary summary = new DailyTeamStatisticSummary(0L, season, d, s, statistics.getMean(), (int) statistics.getN(), statistics.getMin(), statistics.getMax(), statistics.getPercentile(50), statistics.getPercentile(25), statistics.getPercentile(75), statistics.getStandardDeviation(), statistics.getKurtosis(), statistics.getSkewness());
                    return new DailyTeamStatistic(0L, summary, t, value);
                });
            });
        }).collect(Collectors.toList());
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
