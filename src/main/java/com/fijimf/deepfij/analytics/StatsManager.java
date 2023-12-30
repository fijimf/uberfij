package com.fijimf.deepfij.analytics;

import com.fijimf.deepfij.db.model.statistic.DailyTeamStatistic;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatisticSummary;
import com.fijimf.deepfij.db.repo.schedule.SeasonRepo;
import com.fijimf.deepfij.db.repo.statistic.DailyTeamStatisticRepo;
import com.fijimf.deepfij.db.repo.statistic.DailyTeamStatisticSummaryRepo;
import com.fijimf.deepfij.db.repo.statistic.StatisticInserter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class StatsManager {
    private final static Logger logger = LoggerFactory.getLogger(StatsManager.class);
    private final SeasonRepo seasonRepo;
    private final DailyTeamStatisticSummaryRepo summaryRepo;
    private final DailyTeamStatisticRepo statisticRepo;
    private final StatisticInserter inserter;

    public StatsManager(SeasonRepo seasonRepo, DailyTeamStatisticSummaryRepo summaryRepo, DailyTeamStatisticRepo statisticRepo, StatisticInserter inserter) {
        this.seasonRepo = seasonRepo;
        this.summaryRepo = summaryRepo;
        this.statisticRepo = statisticRepo;
        this.inserter = inserter;
    }

    @Transactional
    public void calculateWonLostStats(Integer season) {
        List<DailyTeamStatistic> bulk = new ArrayList<>();
        seasonRepo.findBySeason(season).ifPresent(s -> WonLost.calculate(s).forEach((date, statistics) -> {
            logger.info("Saving won lost stats from date " + date);

            statistics.forEach((summary, byTeam) -> {
                logger.info("Saving " + summary.getStatistic());
                summaryRepo.findBySeasonAndDateAndStatistic(s, date, summary.getStatistic()).ifPresentOrElse(su -> {
                            su.setMin(summary.getMin());
                            su.setMean(summary.getMean());
                            su.setN(summary.getN());
                            su.setMax(summary.getMax());
                            su.setMedian(summary.getMedian());
                            su.setPercentile25(summary.getPercentile25());
                            su.setPercentile75(summary.getPercentile75());
                            su.setStdDev(summary.getStdDev());
                            su.setKurtosis(summary.getKurtosis());
                            su.setSkewness(summary.getSkewness());
                            summaryRepo.saveAndFlush(su);
                            for (DailyTeamStatistic dailyTeamStatistic : byTeam) {
                                dailyTeamStatistic.setSummary(su);
                            }
                        },
                        () -> {
                            DailyTeamStatisticSummary saved = summaryRepo.saveAndFlush(summary);
                            for (DailyTeamStatistic dailyTeamStatistic : byTeam) {
                                dailyTeamStatistic.setSummary(saved);
                            }
                        }
                );

                bulk.addAll(byTeam);

            });
        }));
        logger.info("Inserting " + bulk.size());
        inserter.batchInsertStats(bulk);
        logger.info("Done");
    }
}
