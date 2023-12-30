package com.fijimf.deepfij.db.repo;

import com.fijimf.deepfij.analytics.StatsManager;
import com.fijimf.deepfij.db.repo.schedule.SeasonRepo;
import com.fijimf.deepfij.db.repo.statistic.DailyTeamStatisticRepo;
import com.fijimf.deepfij.db.repo.statistic.DailyTeamStatisticSummaryRepo;
import com.fijimf.deepfij.db.repo.statistic.StatisticInserter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@Testcontainers
@ActiveProfiles("test-containers-jdbc")
@ComponentScan(basePackages = "com.fijimf.deepfij.db.repo")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:sql/basic_season.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class StatsManagerTest {

    @Autowired
    private SeasonRepo seasonRepo;

    @Autowired
    private DailyTeamStatisticSummaryRepo summaryRepo;

    @Autowired
    private DailyTeamStatisticRepo statisticRepo;

    @Autowired
    private StatisticInserter inserter;


    @Test
    void testCalculateWonLostStats() {
        final StatsManager statsManager = new StatsManager(seasonRepo, summaryRepo, statisticRepo, inserter);
        statsManager.calculateWonLostStats(2023);
    }

    @Test
    void isEmptyInitially() {
        assertThat(seasonRepo.count()).isEqualTo(1L);
        assertThat(statisticRepo.count()).isEqualTo(0L);
        assertThat(summaryRepo.count()).isEqualTo(0L);
    }
}