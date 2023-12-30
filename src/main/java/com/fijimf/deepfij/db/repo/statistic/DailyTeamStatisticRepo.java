package com.fijimf.deepfij.db.repo.statistic;

import com.fijimf.deepfij.db.model.schedule.Team;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatistic;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatisticSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DailyTeamStatisticRepo extends JpaRepository<DailyTeamStatistic, Long> {
     int deleteBySummary(DailyTeamStatisticSummary summary);

     @Query(value="from DailyTeamStatistic s where s.team = :team and s.summary.statistic = :stat and s.summary.season.season = :season order by s.summary.date")
     List<DailyTeamStatistic> findByTeamSeasonAndName(@Param("team") Team team, @Param("stat") String stat, @Param("season") Integer season );
}
