package com.fijimf.deepfij.analytics;

import com.fijimf.deepfij.db.model.statistic.DailyTeamStatistic;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatisticSummary;

import java.time.LocalDate;
import java.util.List;

public record DailyStats(LocalDate date, DailyTeamStatisticSummary summary, List<DailyTeamStatistic> teamStats) {
}
