package com.fijimf.deepfij.analytics;

import com.fijimf.deepfij.db.model.schedule.Game;
import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.schedule.Team;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatistic;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatisticSummary;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.fijimf.deepfij.analytics.Points.GameData.*;

public class NormalizedPoints {

    private static final Logger logger = LoggerFactory.getLogger(NormalizedPoints.class);

    //TODO needs a better name
    enum GameMetric {
        NORMALIZED_POINTS_FOR_MEAN, NORMALIZED_POINTS_FOR_VAR,
        NORMALIZED_POINTS_AGAINST_MEAN, NORMALIZED_POINTS_AGAINST_VAR,
        NORMALIZED_MARGIN_MEAN, NORMALIZED_MARGIN_VAR
    }

    //TODO needs a better name
    enum GameData {
        POINTS_FOR, POINTS_AGAINST, MARGIN
    }

    public static SortedMap<LocalDate, Map<DailyTeamStatisticSummary, List<DailyTeamStatistic>>> calculate(Season season) {

        Map<LocalDate, Map<String, Map<Team, DailyTeamStatistic>>> pointsModel =
                Points.calculate(season)
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()
                                .entrySet()
                                .stream()
                                .collect(
                                        Collectors.toMap(
                                                f -> f.getKey().getStatistic(),
                                                f -> f.getValue().stream().collect(Collectors.toMap(DailyTeamStatistic::getTeam, Function.identity()))
                                        )
                                )));


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
                .map(d -> Pair.of(d, calculateTeamStatisticsByDate(season, d, gamesByDate, pointsModel, accumulators)))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight, (v1, v2) -> v1, TreeMap::new));
    }

    private static Map<DailyTeamStatisticSummary, List<DailyTeamStatistic>> calculateTeamStatisticsByDate(Season season, LocalDate d, Map<LocalDate, List<Game>> gamesByDate, Map<LocalDate, Map<String, Map<Team, DailyTeamStatistic>>> pointsModel, Map<Team, Map<GameData, List<Double>>> accumulators) {
        List<Game> games = gamesByDate.getOrDefault(d, Collections.emptyList());
        logger.info("For date {} Updating based on {} games.", d, games.size());
        games.forEach(g -> {
            Team homeTeam = g.getHomeTeam();
            Team awayTeam = g.getAwayTeam();
            DailyTeamStatistic hpfm = getStatistic(d.minusDays(1), pointsModel, homeTeam, "POINTS_FOR_MEAN");
            DailyTeamStatistic hpfv = getStatistic(d.minusDays(1), pointsModel, homeTeam, "POINTS_FOR_VAR");
            DailyTeamStatistic hpam = getStatistic(d.minusDays(1), pointsModel, homeTeam, "POINTS_AGAINST_MEAN");
            DailyTeamStatistic hpav = getStatistic(d, pointsModel, homeTeam, "POINTS_AGAINST_VAR");
            DailyTeamStatistic apfm = getStatistic(d, pointsModel, awayTeam, "POINTS_FOR_MEAN");
            DailyTeamStatistic apfv = getStatistic(d, pointsModel, awayTeam, "POINTS_FOR_VAR");
            DailyTeamStatistic apam = getStatistic(d, pointsModel, awayTeam, "POINTS_AGAINST_MEAN");
            DailyTeamStatistic apav = getStatistic(d, pointsModel, awayTeam, "POINTS_AGAINST_VAR");
            if (ObjectUtils.allNotNull(apfm,apfv,apam,apav) && apfv.getValue()>0.0 && apav.getValue()>0.0) accumulate(g, accumulators, homeTeam, apfm.getValue(), apfv.getValue(), apam.getValue(), apav.getValue());
            if (ObjectUtils.allNotNull(hpfm,hpfv,hpam,hpav) && hpfv.getValue()>0.0 && hpav.getValue()>0.0) accumulate(g, accumulators, awayTeam, hpfm.getValue(), hpfv.getValue(), hpam.getValue(), hpav.getValue());
        });

        Map<Team, Map<GameMetric, Double>> dailyValues = transformAccumulators(accumulators);

        return createSummariesForDate(season, d, dailyValues)
                .stream()
                .map(summary -> createDailyStatistics(dailyValues, summary))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    private static void accumulate(Game g, Map<Team, Map<GameData, List<Double>>> accumulators, Team team, Double oppPFM, Double oppPFV, Double oppPAM, Double oppPAV) {
        accumulators.putIfAbsent(team, new EnumMap<>(GameData.class));
        Map<GameData, List<Double>> teamStats = accumulators.get(team);

        double score = g.getScore(team).doubleValue();
        double oppScore = g.getOppScore(team).doubleValue();

        double normalizedScore = (score - oppPAM) / Math.sqrt(oppPAV);
        double normalizedOppScore = (oppScore - oppPFM) / Math.sqrt(oppPFV);

        teamStats.compute(GameData.POINTS_FOR, appendScore(normalizedScore));
        teamStats.compute(GameData.POINTS_AGAINST, appendScore(normalizedOppScore));
        teamStats.compute(GameData.MARGIN, appendScore(normalizedScore - normalizedOppScore));
    }
    private static BiFunction<GameData, List<Double>, List<Double>> appendScore(Double score) {
        return (k, v) -> {
            List<Double> m = v == null ? new ArrayList<>() : v;
            m.add(score);
            return m;
        };
    }
    private static DailyTeamStatistic getStatistic(LocalDate d, Map<LocalDate, Map<String, Map<Team, DailyTeamStatistic>>> pointsModel, Team homeTeam, String key) {
        return pointsModel.getOrDefault(d, Collections.emptyMap()).getOrDefault(key, Collections.emptyMap()).get(homeTeam);
    }


    private static Map<Team, Map<GameMetric, Double>> transformAccumulators(Map<Team, Map<GameData, List<Double>>> accumulators) {
        Map<Team, Map<GameMetric, Double>> rv = new HashMap<>();
        accumulators.entrySet().forEach(e -> {
            Team k = e.getKey();
            Map<GameData, List<Double>> scoreMap = e.getValue();
            Map<GameMetric, Double> ss = new HashMap<>();
            calcdescriptiveStats(ss, scoreMap, GameData.POINTS_FOR, GameMetric.NORMALIZED_POINTS_FOR_MEAN, GameMetric.NORMALIZED_POINTS_FOR_VAR);
            calcdescriptiveStats(ss, scoreMap, GameData.POINTS_AGAINST, GameMetric.NORMALIZED_POINTS_AGAINST_MEAN, GameMetric.NORMALIZED_POINTS_AGAINST_VAR);
            calcdescriptiveStats(ss, scoreMap, GameData.MARGIN, GameMetric.NORMALIZED_MARGIN_MEAN, GameMetric.NORMALIZED_MARGIN_VAR);
            rv.put(k, ss);
        });
        return rv;
    }

    private static void calcdescriptiveStats(Map<GameMetric, Double> ss, Map<GameData, List<Double>> scoreMap, GameData data, GameMetric mean, GameMetric variance) {

        DescriptiveStatistics d = new DescriptiveStatistics();
        scoreMap.get(data).forEach(d::addValue);
        ss.put(mean, d.getMean());
        ss.put(variance, d.getVariance());
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



}
