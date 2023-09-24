package com.fijimf.deepfij.db.repo;


import com.fijimf.deepfij.db.model.schedule.Game;
import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.schedule.Team;
import com.fijimf.deepfij.db.repo.schedule.GameRepo;
import com.fijimf.deepfij.db.repo.schedule.SeasonRepo;
import com.fijimf.deepfij.db.repo.schedule.TeamRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test-containers")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "create_season.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class GameRepoTest {


    @Autowired
    private SeasonRepo seasonRepo;
    @Autowired
    private TeamRepo teamRepo;
    @Autowired
    private GameRepo gameRepo;

    @Test
    void confirmBaseState() {
        long count = teamRepo.count();
        assertThat(count).isEqualTo(363L);
    }

    @Test
    void createGame() {
        Season season = seasonRepo.findBySeason(2023).orElseThrow();
        Team wyoming = teamRepo.findByEspnIdEquals("2751").orElseThrow();
        Team xavier = teamRepo.findByEspnIdEquals("2752").orElseThrow();

        Game game = gameRepo.saveAndFlush(new Game(
                0L,
                LocalDate.of(2023,2,5),
                LocalDate.of(2023,2,5),
                season,
                wyoming,
                xavier,
                100,
                98,
                2,
                null,
                "Your mom's house",
                -4.5,
                165.0,
                null,
                null,
                "xxxx",
                33L,
                LocalDateTime.now()));
        assertThat(game.getId()).isGreaterThan(0L);
        season = seasonRepo.findBySeason(2023).orElseThrow();
        assertThat(season.getGames()).hasSize(1);
    }
}