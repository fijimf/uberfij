package com.fijimf.deepfij.analytics;

import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatistic;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatisticSummary;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import static com.fijimf.deepfij.analytics.WonLost.GameMetric.values;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * The WonLostTest class tests the calculate method in the WonLost class.
 */
@DataJpaTest
@Testcontainers
@ActiveProfiles("test-containers-jdbc")
@ComponentScan(basePackages = "com.fijimf.deepfij.db.repo")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:sql/basic_season.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class WonLostTest {

    @Autowired
    private SeasonRepo seasonRepo;

    @Autowired
    private DailyTeamStatisticSummaryRepo summaryRepo;

    @Autowired
    private DailyTeamStatisticRepo statisticRepo;

    @Autowired
    private StatisticInserter inserter;


    /**
     * Test for the calculate method of WonLost class.
     */
    @Test
    void testCalculate() {
        // Initialize required objects for testing
        Season season = seasonRepo.findById(2L).orElseThrow();

        // Call calculate method
        List<DailyStats> result = WonLost.calculate(season);

        // Verifying the calculation results
        assertFalse(result.isEmpty(), "WonLost.calculate() result should not be empty");
        assertEquals(148 * 5, result.size(), "Wrong number of dates in the result");
        assertThat(result).allSatisfy((s) -> assertThat(s.summary().getN()).isEqualTo(s.teamStats().size()));
        //

        Map<String, Map<LocalDate, Map<String, Double>>>tests = Map.of(
                "WINS",
                Map.of(
                        LocalDate.of(2022, 11, 30),
                        Map.of("georgetown-hoyas", 4.0, "virginia-cavaliers", 6.0),
                        LocalDate.of(2022, 12, 25),
                        Map.of("georgetown-hoyas", 5.0, "virginia-cavaliers", 8.0),
                        LocalDate.of(2023, 2, 5),
                        Map.of("georgetown-hoyas", 6.0, "virginia-cavaliers", 17.0),
                        LocalDate.of(2023, 3, 31),
                        Map.of("georgetown-hoyas", 7.0, "virginia-cavaliers", 25.0)
                ), "LOSSES",
                Map.of(
                        LocalDate.of(2022, 11, 30),
                        Map.of("georgetown-hoyas", 3.0, "virginia-cavaliers", 0.0),
                        LocalDate.of(2022, 12, 25),
                        Map.of("georgetown-hoyas", 8.0, "virginia-cavaliers", 2.0),
                        LocalDate.of(2023, 2, 5),
                        Map.of("georgetown-hoyas", 17.0, "virginia-cavaliers", 3.0),
                        LocalDate.of(2023, 3, 31),
                        Map.of("georgetown-hoyas", 24.0, "virginia-cavaliers", 7.0)
                ), "LOSS_STREAK",
                Map.of(
                        LocalDate.of(2022, 11, 30),
                        Map.of("georgetown-hoyas", 0.0, "virginia-cavaliers", 0.0),
                        LocalDate.of(2022, 12, 25),
                        Map.of("georgetown-hoyas", 3.0, "virginia-cavaliers", 2.0),
                        LocalDate.of(2023, 2, 5),
                        Map.of("georgetown-hoyas", 2.0, "virginia-cavaliers", 0.0),
                        LocalDate.of(2023, 3, 31),
                        Map.of("georgetown-hoyas", 4.0, "virginia-cavaliers", 2.0)
                ), "WIN_STREAK",
                Map.of(
                        LocalDate.of(2022, 11, 30),
                        Map.of("georgetown-hoyas", 1.0, "virginia-cavaliers", 6.0),
                        LocalDate.of(2022, 12, 25),
                        Map.of("georgetown-hoyas", 0.0, "virginia-cavaliers", 0.0),
                        LocalDate.of(2023, 2, 5),
                        Map.of("georgetown-hoyas", 0.0, "virginia-cavaliers", 7.0),
                        LocalDate.of(2023, 3, 31),
                        Map.of("georgetown-hoyas", 0.0, "virginia-cavaliers", 0.0)
                ), "WIN_PCT",
                Map.of(
                        LocalDate.of(2022, 11, 30),
                        Map.of("georgetown-hoyas", 0.5714285714285714, "virginia-cavaliers", 1.0),
                        LocalDate.of(2022, 12, 25),
                        Map.of("georgetown-hoyas", 0.38461538461538464, "virginia-cavaliers", 0.8),
                        LocalDate.of(2023, 2, 5),
                        Map.of("georgetown-hoyas", 0.2608695652173913, "virginia-cavaliers", 0.85),
                        LocalDate.of(2023, 3, 31),
                        Map.of("georgetown-hoyas", 0.22580645161290322, "virginia-cavaliers", 0.78125)
                )
        );

        result.forEach((d) -> {
            Map<LocalDate, Map<String, Double>> byDate = tests.getOrDefault(d.summary().getStatistic(), Collections.emptyMap());
            Map<String, Double> byTeam = byDate.getOrDefault(d.date(), Collections.emptyMap());
            d.teamStats().forEach((s) -> {
                if (byTeam.containsKey(s.getTeam().getKey())) {
                    Double value = byTeam.get(s.getTeam().getKey());
                    assertThat(s.getValue()).as(d.summary().getStatistic() + "  " + s.getTeam().getKey() + "   " + d.date() + "    " + value).isEqualTo(value);
                }
               });
        });

    }
}