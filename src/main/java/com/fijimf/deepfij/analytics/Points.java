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

import static com.fijimf.deepfij.analytics.Points.GameData.*;
import static com.fijimf.deepfij.analytics.Points.GameMetric.*;

public class Points implements StatisticCalculator {

    private static final Logger logger = LoggerFactory.getLogger(Points.class);

    //TODO needs a better name
    enum GameMetric {
        POINTS_FOR_MEAN, POINTS_FOR_VAR, POINTS_FOR_MAX, POINTS_FOR_MIN,
        POINTS_AGAINST_MEAN, POINTS_AGAINST_VAR, POINTS_AGAINST_MAX, POINTS_AGAINST_MIN,
        MARGIN_MEAN, MARGIN_VAR, MARGIN_MAX, MARGIN_MIN,
    }

    //TODO needs a better name
    enum GameData {
        POINTS_FOR, POINTS_AGAINST, MARGIN
    }

    public static SortedMap<LocalDate, Map<DailyTeamStatisticSummary, List<DailyTeamStatistic>>> calculate(Season season) {
        Map<LocalDate, List<Game>> gamesByDate = season
                .getGames()
                .stream()
                .filter(Game::isComplete)
                .collect(Collectors.groupingBy(Game::getDate));

        Map<Team, Map<GameData, List<Double>>> accumulators = new HashMap<>();
        LocalDate startDate = gamesByDate.keySet().stream().min(Comparator.naturalOrder()).orElse(season.defaultStartDate());
        LocalDate endDate = gamesByDate.keySet().stream().max(Comparator.naturalOrder()).orElse(season.defaultEndDate());
        return Stream
                .iterate(startDate, d -> d.isBefore(endDate), d -> d.plusDays(1))
                .map(d -> Pair.of(d, calculateTeamStatisticsByDate(season, d, gamesByDate, accumulators)))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight, (v1, v2) -> v1, TreeMap::new));
    }

    private static Map<DailyTeamStatisticSummary, List<DailyTeamStatistic>> calculateTeamStatisticsByDate(Season season, LocalDate d, Map<LocalDate, List<Game>> gamesByDate, Map<Team, Map<GameData, List<Double>>> accumulators) {
        List<Game> games = gamesByDate.getOrDefault(d, Collections.emptyList());
        logger.info("For date {} Updating based on {} games.", d, games.size());
        games.forEach(g -> {
            accumulate(g, accumulators, g.getHomeTeam());
            accumulate(g, accumulators, g.getAwayTeam());
        });

        Map<Team, Map<GameMetric, Double>> dailyValues = transformAccumulators(accumulators);

        return createSummariesForDate(season, d, dailyValues)
                .stream()
                .map(summary -> createDailyStatistics(dailyValues, summary))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    private static Map<Team, Map<GameMetric, Double>> transformAccumulators(Map<Team, Map<GameData, List<Double>>> accumulators) {
        Map<Team, Map<GameMetric, Double>> rv = new HashMap<>();
        accumulators.entrySet().forEach(e -> {
            Team k = e.getKey();
            Map<GameData, List<Double>> scoreMap = e.getValue();
            Map<GameMetric, Double> ss = new HashMap<>();
            calcdescriptiveStats(ss, scoreMap, POINTS_FOR, POINTS_FOR_MEAN, POINTS_FOR_VAR, POINTS_FOR_MIN, POINTS_FOR_MAX);
            calcdescriptiveStats(ss, scoreMap, POINTS_AGAINST, POINTS_AGAINST_MEAN, POINTS_AGAINST_VAR, POINTS_AGAINST_MIN, POINTS_AGAINST_MAX);
            calcdescriptiveStats(ss, scoreMap, MARGIN, MARGIN_MEAN, MARGIN_VAR, MARGIN_MIN, MARGIN_MAX);
            rv.put(k, ss);
        });
        return rv;
    }

    private static void calcdescriptiveStats(Map<GameMetric, Double> ss, Map<GameData, List<Double>> scoreMap, GameData data, GameMetric mean, GameMetric variance, GameMetric min, GameMetric max) {

        DescriptiveStatistics d = new DescriptiveStatistics();
        scoreMap.get(data).forEach(d::addValue);
        ss.put(mean, d.getMean());
        ss.put(variance, d.getVariance());
        ss.put(max, d.getMax());
        ss.put(min, d.getMin());

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
        return Arrays.stream(GameMetric.values()).map(statName -> {
            DescriptiveStatistics statistics = new DescriptiveStatistics();
            for (Team team : runningTotal.keySet()) {
                statistics.addValue(runningTotal.get(team).get(statName));
            }
            return new DailyTeamStatisticSummary(0L, season, d, statName.name(), statistics.getMean(), (int) statistics.getN(), statistics.getMin(), statistics.getMax(), statistics.getPercentile(50), statistics.getPercentile(25), statistics.getPercentile(75), statistics.getStandardDeviation(), statistics.getKurtosis(), statistics.getSkewness());
        }).toList();
    }

    private static BiFunction<GameData, List<Double>, List<Double>> appendScore(Double score) {
        return (k, v) -> {
            List<Double> m = v == null ? new ArrayList<>() : v;
            m.add(score);
            return m;
        };
    }

    private static void accumulate(Game g, Map<Team, Map<GameData, List<Double>>> runningTotal, Team team) {
        runningTotal.putIfAbsent(team, new EnumMap<>(GameData.class));
        Map<GameData, List<Double>> teamStats = runningTotal.get(team);

        double score = g.getScore(team).doubleValue();
        double oppScore = g.getOppScore(team).doubleValue();

        teamStats.compute(POINTS_FOR, appendScore(score));
        teamStats.compute(POINTS_AGAINST, appendScore(oppScore));
        teamStats.compute(MARGIN, appendScore(score - oppScore));
    }
}
