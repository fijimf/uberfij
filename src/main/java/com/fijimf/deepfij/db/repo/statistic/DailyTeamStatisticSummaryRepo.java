package com.fijimf.deepfij.db.repo.statistic;

import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatisticSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyTeamStatisticSummaryRepo extends JpaRepository<DailyTeamStatisticSummary, Long> {

    Optional<DailyTeamStatisticSummary> findBySeasonAndDateAndStatistic(Season s, LocalDate date, String statistic);
    void deleteBySeasonAndDateAndStatistic(Season s, LocalDate date, String statistic);

}
