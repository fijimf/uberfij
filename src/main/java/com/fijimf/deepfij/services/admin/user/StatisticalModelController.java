package com.fijimf.deepfij.services.admin.user;

import com.fijimf.deepfij.analytics.WonLost;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatistic;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatisticSummary;
import com.fijimf.deepfij.db.repo.schedule.SeasonRepo;
import com.fijimf.deepfij.db.repo.statistic.DailyTeamStatisticRepo;
import com.fijimf.deepfij.db.repo.statistic.DailyTeamStatisticSummaryRepo;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stats")
public class StatisticalModelController {
    private final static Logger logger = LoggerFactory.getLogger(StatisticalModelController.class);
    private final SeasonRepo seasonRepo;
    private final DailyTeamStatisticSummaryRepo summaryRepo;
    private final DailyTeamStatisticRepo statisticRepo;

    public StatisticalModelController(SeasonRepo seasonRepo, DailyTeamStatisticSummaryRepo summaryRepo, DailyTeamStatisticRepo statisticRepo) {
        this.seasonRepo = seasonRepo;
        this.summaryRepo = summaryRepo;
        this.statisticRepo = statisticRepo;
    }

    @PostMapping("/calc/wonlost")
    public void calculateWonLostStats(@RequestParam("season") Integer season, HttpServletResponse response) {
        Map<LocalDate, Map<DailyTeamStatisticSummary, List<DailyTeamStatistic>>> data = seasonRepo.findBySeason(season).map(WonLost::calculate).orElse(Collections.emptyMap());
        data.forEach((date, map) -> {
            logger.info("saving won lost stats from date " + date);
            map.forEach((summary, list) -> {
                DailyTeamStatisticSummary saved = summaryRepo.saveAndFlush(summary);
                for (DailyTeamStatistic dailyTeamStatistic : list) {
                    dailyTeamStatistic.setSummary(saved);
                    statisticRepo.saveAndFlush(dailyTeamStatistic);
                }
            });
        });
        response.setStatus(HttpServletResponse.SC_OK);
    }
}

