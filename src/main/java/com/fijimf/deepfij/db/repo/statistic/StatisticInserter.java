package com.fijimf.deepfij.db.repo.statistic;

import com.fijimf.deepfij.db.model.statistic.DailyTeamStatistic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
public class StatisticInserter {

    private final JdbcTemplate template;

    public StatisticInserter(@Autowired JdbcTemplate template) {
        this.template = template;
    }

    public void batchInsertStats(List<DailyTeamStatistic> stats) {
        String sql = "INSERT INTO daily_team_statistic ( summary_id, team_id, value) VALUES ( ?, ?, ?)";
        template.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                DailyTeamStatistic stat = stats.get(i);
                ps.setLong(1, stat.getSummary().getId());
                ps.setLong(2, stat.getTeam().getId());
                ps.setDouble(3, stat.getValue());
            }

            @Override
            public int getBatchSize() {
                return stats.size();
            }
        });
    }
}
