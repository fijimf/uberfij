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
                summaryRepo.findBySeasonAndDateAndStatistic(s, date, summary.getStatistic())
                        .ifPresentOrElse(
                                oldSummary -> {
                                    logger.info("Found summary "+oldSummary.getId()+":["+oldSummary.getStatistic()+", "+oldSummary.getDate()+"]");
                                    deleteOldStats(oldSummary);
                                    updateSummaryStats (summary, oldSummary, byTeam);
                                },
                                () -> insertSummaryStats(summary, byTeam)
                        );
                bulk.addAll(byTeam);
            });
        }));
        logger.info("Inserting " + bulk.size());
        inserter.batchInsertStats(bulk);
        logger.info("Done");
    }

    @Transactional
    public void deleteOldStats(DailyTeamStatisticSummary oldSummary) {
        int numDeleted = statisticRepo.deleteBySummary(oldSummary);
        logger.info("Deleted "+numDeleted+" daily statistics");
    }

    @Transactional
    public void insertSummaryStats(DailyTeamStatisticSummary summary, List<DailyTeamStatistic> byTeam){
        DailyTeamStatisticSummary saved = summaryRepo.saveAndFlush(summary);
        for (DailyTeamStatistic dailyTeamStatistic : byTeam) {
            dailyTeamStatistic.setSummary(saved);
        }
    }

    @Transactional
    public void updateSummaryStats(DailyTeamStatisticSummary summary, DailyTeamStatisticSummary oldSummary, List<DailyTeamStatistic> byTeam) {
        oldSummary.setMin(summary.getMin());
        oldSummary.setMean(summary.getMean());
        oldSummary.setN(summary.getN());
        oldSummary.setMax(summary.getMax());
        oldSummary.setMedian(summary.getMedian());
        oldSummary.setPercentile25(summary.getPercentile25());
        oldSummary.setPercentile75(summary.getPercentile75());
        oldSummary.setStdDev(summary.getStdDev());
        oldSummary.setKurtosis(summary.getKurtosis());
        oldSummary.setSkewness(summary.getSkewness());
        summaryRepo.saveAndFlush(oldSummary);
        logger.info("Setting summaryId to "+oldSummary.getId()+ " for "+byTeam.size()+" statistics");
        for (DailyTeamStatistic dailyTeamStatistic : byTeam) {
            dailyTeamStatistic.setSummary(oldSummary);
        }
    }
}
