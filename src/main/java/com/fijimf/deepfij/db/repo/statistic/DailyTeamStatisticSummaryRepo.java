package com.fijimf.deepfij.db.repo.statistic;

import com.fijimf.deepfij.db.model.statistic.DailyTeamStatisticSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyTeamStatisticSummaryRepo extends JpaRepository<DailyTeamStatisticSummary, Long> {
}
