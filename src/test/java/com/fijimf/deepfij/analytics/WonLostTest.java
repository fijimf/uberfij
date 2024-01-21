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
        SortedMap<LocalDate, Map<DailyTeamStatisticSummary, List<DailyTeamStatistic>>> result = WonLost.calculate(season);

        // Verifying the calculation results
        assertFalse(result.isEmpty(), "WonLost.calculate() result should not be empty");
        assertEquals(148, result.size(), "Wrong number of dates in the result");
        assertThat(result).allSatisfy((d, s) -> assertThat(s).hasSize(values().length));
        //

        Map<String, Map<String, Map<LocalDate, Double>>> testCases =
                Map.of("WINS",
                        Map.of("georgetown-hoyas",
                                Map.of(
                                        LocalDate.of(2022, 11, 30), 4.0,
                                        LocalDate.of(2022, 12, 25), 5.0,
                                        LocalDate.of(2023, 2, 5), 6.0,
                                        LocalDate.of(2023, 3, 31), 7.0),
                                "virginia-cavaliers",
                                Map.of(
                                        LocalDate.of(2022, 11, 30), 6.0,
                                        LocalDate.of(2022, 12, 25), 8.0,
                                        LocalDate.of(2023, 2, 5), 17.0,
                                        LocalDate.of(2023, 3, 31), 25.0)
                        ),
                        "LOSSES",
                        Map.of("georgetown-hoyas",
                                Map.of(
                                        LocalDate.of(2022, 11, 30), 3.0,
                                        LocalDate.of(2022, 12, 25), 8.0,
                                        LocalDate.of(2023, 2, 5), 17.0,
                                        LocalDate.of(2023, 3, 31), 24.0),
                                "virginia-cavaliers",
                                Map.of(
                                        LocalDate.of(2022, 11, 30), 0.0,
                                        LocalDate.of(2022, 12, 25), 2.0,
                                        LocalDate.of(2023, 2, 5), 3.0,
                                        LocalDate.of(2023, 3, 31), 7.0)
                        ),
                        "LOSS_STREAK",
                        Map.of("georgetown-hoyas",
                                Map.of(
                                        LocalDate.of(2022, 11, 30), 0.0,
                                        LocalDate.of(2022, 12, 25), 3.0,
                                        LocalDate.of(2023, 2, 5), 2.0,
                                        LocalDate.of(2023, 3, 31), 4.0),
                                "virginia-cavaliers",
                                Map.of(
                                        LocalDate.of(2022, 11, 30), 0.0,
                                        LocalDate.of(2022, 12, 25), 2.0,
                                        LocalDate.of(2023, 2, 5), 0.0,
                                        LocalDate.of(2023, 3, 31), 2.0)
                        )
                );

        testCases.forEach((stat, map) -> {
            map.forEach((team, dateMap) -> {
                dateMap.forEach((date, value) -> {
                    Map<DailyTeamStatisticSummary, List<DailyTeamStatistic>> byDate = result.get(date);
                    DailyTeamStatisticSummary statisticSummary = byDate.keySet().stream().filter(s -> s.getStatistic().equalsIgnoreCase(stat)).findFirst().orElseThrow();
                    assertThat(statisticSummary).isNotNull();
                    List<DailyTeamStatistic> data = byDate.getOrDefault(statisticSummary, null);
                    assertThat(data).isNotNull();
                    DailyTeamStatistic dailyStatistic = data.stream().filter(w -> w.getTeam().getKey().equalsIgnoreCase(team)).findFirst().orElseThrow();
                    assertThat(dailyStatistic.getValue()).as(stat + "  " + team + "   " + date + "    " + value).isEqualTo(value);

                });
            });
        });

    }
}