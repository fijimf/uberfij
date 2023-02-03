package com.fijimf.deepfij.db.repo;


import com.fijimf.deepfij.db.model.schedule.Conference;
import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.repo.schedule.SeasonRepo;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test-containers")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SeasonRepoTest {


    @Autowired
    private SeasonRepo seasonRepo;

    @BeforeEach
    void init() {
        seasonRepo.deleteAll();
    }

    @Test
    void isEmptyInitially() {
        long count = seasonRepo.count();
        assertThat(count).isEqualTo(0L);
    }

    @Test
    void saveOne() {
        Season season = new Season(0L, 2023);
        assertThat(season.getId()).isEqualTo(0L);
        season = seasonRepo.saveAndFlush(season);
        assertThat(season.getId()).isGreaterThan(0L);
        assertThat(seasonRepo.count()).isEqualTo(1);
    }

    @Test
    void saveMany() {
        List<Season> seasons = IntStream.range(1, 35).mapToObj(n -> new Season(0L, 1990+n)).toList();
        assertThat(seasons).allMatch(c -> c.getId() == 0);
        seasons = seasonRepo.saveAllAndFlush(seasons);
        assertThat(seasons).allMatch(c -> c.getId() > 0);
        assertThat(seasonRepo.count()).isEqualTo(34);
    }
}