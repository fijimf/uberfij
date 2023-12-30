package com.fijimf.deepfij.analytics;

import com.fijimf.deepfij.db.model.schedule.Game;
import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.schedule.Team;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatistic;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatisticSummary;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WonLost {

    private static final Logger logger = LoggerFactory.getLogger(WonLost.class);
    public static final String WINS = "Wins";
    public static final String LOSSES = "Losses";
    public static final String WIN_PCT = "WinPct";
    public static final String WIN_STREAK = "WinStreak";
    public static final String LOSS_STREAK = "LossStreak";

    public static SortedMap<LocalDate, Map<DailyTeamStatisticSummary, List<DailyTeamStatistic>>> calculate(Season season) {
        Map<LocalDate, List<Game>> gamesByDate = season
                .getGames()
                .stream()
                .filter(Game::isComplete)
                .collect(Collectors.groupingBy(Game::getDate));

        Map<Team, Map<String, Double>> runningTotal = new HashMap<>();
        LocalDate startDate = gamesByDate.keySet().stream().min(Comparator.naturalOrder()).orElse(season.defaultStartDate());
        LocalDate endDate = gamesByDate.keySet().stream().max(Comparator.naturalOrder()).orElse(season.defaultEndDate());
        return Stream
                .iterate(startDate, d -> d.isBefore(endDate), d -> d.plusDays(1))
                .map(d -> Pair.of(d, calculateTeamStatisticsByDate(season, d, gamesByDate, runningTotal)))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight, (v1, v2) -> v1, TreeMap::new));
    }

    private static Map<DailyTeamStatisticSummary, List<DailyTeamStatistic>> calculateTeamStatisticsByDate(Season season, LocalDate d, Map<LocalDate, List<Game>> gamesByDate, Map<Team, Map<String, Double>> runningTotal) {
        List<Game> games = gamesByDate.getOrDefault(d, Collections.emptyList());
        logger.info("For date {} Updating based on {} games.", d, games.size());
        games.forEach(g -> {
            accumulate(g, runningTotal, g.getHomeTeam());
            accumulate(g, runningTotal, g.getAwayTeam());
        });

        return createSummariesForDate(season, d, runningTotal)
                .stream()
                .map(summary -> createDailyStatistics(runningTotal, summary))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    private static Pair<DailyTeamStatisticSummary, List<DailyTeamStatistic>> createDailyStatistics(Map<Team, Map<String, Double>> runningTotal, DailyTeamStatisticSummary summary) {
        List<DailyTeamStatistic> dailyTeamStatistics = runningTotal.entrySet().stream().map(entry -> {
            Team team = entry.getKey();
            Map<String, Double> values = entry.getValue();
            if (values.containsKey(summary.getStatistic())) {
                Double value = values.get(summary.getStatistic());
                return new DailyTeamStatistic(0L, summary, team, value);
            } else {
                return null;
            }
        }).filter(Objects::nonNull).toList();
        return Pair.of(summary, dailyTeamStatistics);
    }

    private static List<DailyTeamStatisticSummary> createSummariesForDate(Season season, LocalDate d, Map<Team, Map<String, Double>> runningTotal) {
        return Stream.of(WINS, LOSSES, WIN_PCT, WIN_STREAK, LOSS_STREAK).map(statName -> {
            DescriptiveStatistics statistics = new DescriptiveStatistics();
            for (Team team : runningTotal.keySet()) {
                statistics.addValue(runningTotal.get(team).get(statName));
            }
            return new DailyTeamStatisticSummary(0L, season, d, statName, statistics.getMean(), (int) statistics.getN(), statistics.getMin(), statistics.getMax(), statistics.getPercentile(50), statistics.getPercentile(25), statistics.getPercentile(75), statistics.getStandardDeviation(), statistics.getKurtosis(), statistics.getSkewness());
        }).toList();
    }

    private static void accumulate(Game g, Map<Team, Map<String, Double>> runningTotal, Team team) {
        runningTotal.putIfAbsent(team, new HashMap<>());
        Map<String, Double> teamStats = runningTotal.get(team);
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
