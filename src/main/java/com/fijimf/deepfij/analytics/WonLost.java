package com.fijimf.deepfij.analytics;

import com.fijimf.deepfij.db.model.schedule.Game;
import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.schedule.Team;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatistic;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatisticSummary;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.fijimf.deepfij.util.Utils.dateStream;

public class WonLost implements StatisticCalculator {

    private static final Logger logger = LoggerFactory.getLogger(WonLost.class);

    enum GameMetric {
        WINS, LOSSES, WIN_STREAK, LOSS_STREAK, WIN_PCT
    }

    public static List<DailyStats> calculate(Season season) {
        Map<LocalDate, List<Game>> gamesByDate = StatisticCalculator.getGamesByDate(season);
        LocalDate startDate = StatisticCalculator.getStartDate(gamesByDate, season);
        LocalDate endDate = StatisticCalculator.getEndDate(gamesByDate, season);
        DailyStatsEmitter emitter = new DailyStatsEmitter(season);
        Stream<LocalDate> localDateStream = dateStream(startDate, endDate);
        return localDateStream
                .flatMap(d -> emitter.emit(d, gamesByDate.getOrDefault(d, Collections.emptyList())))
                .toList();
    }

    public static class DailyStatsEmitter {
        private final Season season;
        private final Map<Team, Double> wins = new HashMap<>();
        private final Map<Team, Double> losses = new HashMap<>();
        private final Map<Team, Double> winStreak = new HashMap<>();
        private final Map<Team, Double> lossStreak = new HashMap<>();
        private LocalDate date;

        public DailyStatsEmitter(Season season) {
            this.season = season;
        }

        public Stream<DailyStats> emit(LocalDate d, List<Game> games) {
            if (date == null || date.equals(d.minusDays(1L))) {
                date = d;
                logger.info("For date {} Updating based on {} games.", d, games.size());
                games.forEach(g -> {
                    accumulate(g, g.getHomeTeam());
                    accumulate(g, g.getAwayTeam());
                });
                return Stream.of(
                        createStats(d, "WINS", wins),
                        createStats(d, "LOSSES", losses),
                        createStats(d, "WIN_STREAK", winStreak),
                        createStats(d, "LOSS_STREAK", lossStreak),
                        createStats(d, "WIN_PCT", calculateWinPct())
                );
            } else {
                throw new IllegalArgumentException("Cannot emit on a different date");
            }
        }

        private Map<Team, Double> calculateWinPct() {
            return wins.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() / (e.getValue() + losses.get(e.getKey()))));
        }

        private DailyStats createStats(LocalDate d, String key, Map<Team, Double> values) {
            DescriptiveStatistics stats = new DescriptiveStatistics();
            values.values().forEach(stats::addValue);
            DailyTeamStatisticSummary summary = new DailyTeamStatisticSummary(
                    0L, season, d, key, stats.getMean(), (int) stats.getN(), stats.getMin(), stats.getMax(),
                    stats.getPercentile(50), stats.getPercentile(25), stats.getPercentile(75),
                    stats.getStandardDeviation(), stats.getKurtosis(), stats.getSkewness()
            );
            List<DailyTeamStatistic> statistics = values.entrySet().stream()
                    .map(e -> new DailyTeamStatistic(0L, summary, e.getKey(), e.getValue()))
                    .toList();
            return new DailyStats(d, summary, statistics);
        }

        private void accumulate(Game g, Team team) {
            wins.putIfAbsent(team, 0.0);
            losses.putIfAbsent(team, 0.0);
            winStreak.putIfAbsent(team, 0.0);
            lossStreak.putIfAbsent(team, 0.0);

            if (g.isWinner(team)) {
                wins.merge(team, 1.0, Double::sum);
                winStreak.merge(team, 1.0, Double::sum);
                lossStreak.put(team, 0.0);
            } else {
                losses.merge(team, 1.0, Double::sum);
                lossStreak.merge(team, 1.0, Double::sum);
                winStreak.put(team, 0.0);
            }
        }
    }
}