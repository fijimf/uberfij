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
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.fijimf.deepfij.analytics.WonLost.GameMetric.*;

public class WonLost {

    private static final Logger logger = LoggerFactory.getLogger(WonLost.class);

    enum GameMetric {
        WINS, LOSSES, WIN_STREAK, LOSS_STREAK, WIN_PCT
    }

    public static SortedMap<LocalDate, Map<DailyTeamStatisticSummary, List<DailyTeamStatistic>>> calculate(Season season) {
        Map<LocalDate, List<Game>> gamesByDate = season
                .getGames()
                .stream()
                .filter(Game::isComplete)
                .collect(Collectors.groupingBy(Game::getDate));

        Map<Team, Map<GameMetric, Double>> runningTotal = new HashMap<>();
        LocalDate startDate = gamesByDate.keySet().stream().min(Comparator.naturalOrder()).orElse(season.defaultStartDate());
        LocalDate endDate = gamesByDate.keySet().stream().max(Comparator.naturalOrder()).orElse(season.defaultEndDate());
        return Stream
                .iterate(startDate, d -> d.isBefore(endDate), d -> d.plusDays(1))
                .map(d -> Pair.of(d, calculateTeamStatisticsByDate(season, d, gamesByDate, runningTotal)))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight, (v1, v2) -> v1, TreeMap::new));
    }

    private static Map<DailyTeamStatisticSummary, List<DailyTeamStatistic>> calculateTeamStatisticsByDate(Season season, LocalDate d, Map<LocalDate, List<Game>> gamesByDate, Map<Team, Map<GameMetric, Double>> runningTotal) {
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

    private static Pair<DailyTeamStatisticSummary, List<DailyTeamStatistic>> createDailyStatistics(Map<Team, Map<GameMetric, Double>> runningTotal, DailyTeamStatisticSummary summary) {
        List<DailyTeamStatistic> dailyTeamStatistics = runningTotal.entrySet().stream().map(entry -> {
            Team team = entry.getKey();
            Map<GameMetric, Double> values = entry.getValue();
            if (values.containsKey(GameMetric.valueOf(summary.getStatistic()))) {
                Double value = values.get(GameMetric.valueOf(summary.getStatistic()));
                return new DailyTeamStatistic(0L, summary, team, value);
            } else {
                return null;
            }
        }).filter(Objects::nonNull).toList();
        return Pair.of(summary, dailyTeamStatistics);
    }

    private static List<DailyTeamStatisticSummary> createSummariesForDate(Season season, LocalDate d, Map<Team, Map<GameMetric, Double>> runningTotal) {
        return Stream.of(WINS, LOSSES, WIN_PCT, WIN_STREAK, LOSS_STREAK).map(statName -> {
            DescriptiveStatistics statistics = new DescriptiveStatistics();
            for (Team team : runningTotal.keySet()) {
                statistics.addValue(runningTotal.get(team).get(statName));
            }
            return new DailyTeamStatisticSummary(0L, season, d, statName.name(), statistics.getMean(), (int) statistics.getN(), statistics.getMin(), statistics.getMax(), statistics.getPercentile(50), statistics.getPercentile(25), statistics.getPercentile(75), statistics.getStandardDeviation(), statistics.getKurtosis(), statistics.getSkewness());
        }).toList();
    }


    private static void accumulate(Game g, Map<Team, Map<GameMetric, Double>> runningTotal, Team team) {
        runningTotal.putIfAbsent(team, new EnumMap<>(GameMetric.class));
        Map<GameMetric, Double> teamStats = runningTotal.get(team);
        BiFunction<GameMetric, Double, Double> increment = (k, v) -> (v == null ? 1 : v + 1.0);
        if (team.getKey().equalsIgnoreCase("georgetown-hoyas")) {
            logger.info("**Updating hoyas " + g.getDate() + " hoyas winner? " + g.isWinner(team));
            logger.info(" Before " + teamStats.getOrDefault(WINS, 0.0) + " - " + teamStats.getOrDefault(LOSSES, 0.0));
        }
        if (g.isWinner(team)) {
            teamStats.compute(WINS, increment);
            teamStats.putIfAbsent(LOSSES, 0.0);
            teamStats.compute(WIN_STREAK, increment);
            teamStats.put(LOSS_STREAK, 0.0);
        } else {
            teamStats.compute(LOSSES, increment);
            teamStats.putIfAbsent(WINS, 0.0);
            teamStats.compute(LOSS_STREAK, increment);
            teamStats.put(WIN_STREAK, 0.0);
        }
        teamStats.put(GameMetric.WIN_PCT, teamStats.get(GameMetric.WINS) / (teamStats.get(GameMetric.WINS) + teamStats.get(GameMetric.LOSSES)));
        if (team.getKey().equalsIgnoreCase("georgetown-hoyas")) {
            logger.info(" After " + teamStats.getOrDefault(WINS, 0.0) + " - " + teamStats.getOrDefault(LOSSES, 0.0));
        }
    }
}
