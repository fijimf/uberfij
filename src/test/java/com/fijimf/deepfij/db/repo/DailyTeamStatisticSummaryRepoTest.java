package com.fijimf.deepfij.db.repo;


import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatisticSummary;
import com.fijimf.deepfij.db.repo.schedule.SeasonRepo;
import com.fijimf.deepfij.db.repo.statistic.DailyTeamStatisticSummaryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test-containers")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = { "classpath:sql/basic_season.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DailyTeamStatisticSummaryRepoTest {

    @Autowired
    private DailyTeamStatisticSummaryRepo dailyTeamStatisticSummaryRepo;
    @Autowired
    private SeasonRepo seasonRepo;

    @BeforeEach
    void init() {
        dailyTeamStatisticSummaryRepo.deleteAll();
    }

    @Test
    void repositoryIsEmptyInitially() {
        long count = dailyTeamStatisticSummaryRepo.count();
        assertThat(count).isEqualTo(0L);
    }

    @Test
    void saveStatisticSummary() {
        Season season = seasonRepo.findBySeason(2024).orElseThrow();
        DailyTeamStatisticSummary summary = new DailyTeamStatisticSummary(0L, season, LocalDate.of(2023,11,25),"WINS",0.5,2,0.0,1.0,0.5,0.0,1.0,0.25,0.0,0.0);
        summary = dailyTeamStatisticSummaryRepo.saveAndFlush(summary);
        assertThat(summary.getId()).isGreaterThan(0L);
    }

    @Test
    void findStatisticSummaryById() {
        Season season = seasonRepo.findBySeason(2024).orElseThrow();
        DailyTeamStatisticSummary summary = new DailyTeamStatisticSummary(0L, season, LocalDate.of(2023,11,25),"WINS",0.5,2,0.0,1.0,0.5,0.0,1.0,0.25,0.0,0.0);
        summary = dailyTeamStatisticSummaryRepo.saveAndFlush(summary);
        Optional<DailyTeamStatisticSummary> found = dailyTeamStatisticSummaryRepo.findById(summary.getId());
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getId()).isEqualTo(summary.getId());
    }

    @Test
    void deleteStatisticSummary() {
        Season season = seasonRepo.findBySeason(2024).orElseThrow();
        DailyTeamStatisticSummary summary = new DailyTeamStatisticSummary(0L, season, LocalDate.of(2023,11,25),"WINS",0.5,2,0.0,1.0,0.5,0.0,1.0,0.25,0.0,0.0);
        summary = dailyTeamStatisticSummaryRepo.saveAndFlush(summary);
        assertThat(summary.getId()).isGreaterThan(0L);
        dailyTeamStatisticSummaryRepo.delete(summary);
        Optional<DailyTeamStatisticSummary> found = dailyTeamStatisticSummaryRepo.findById(summary.getId());
        assertThat(found.isPresent()).isFalse();
    }
}
