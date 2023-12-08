package com.fijimf.deepfij.db.repo.statistic;

import com.fijimf.deepfij.db.model.statistic.DailyTeamStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyTeamStatisticRepo extends JpaRepository<DailyTeamStatistic, Long> {
}
