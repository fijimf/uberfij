package com.fijimf.deepfij.db.repo;


import com.fijimf.deepfij.db.model.schedule.Conference;
import com.fijimf.deepfij.db.model.schedule.Team;
import com.fijimf.deepfij.db.repo.schedule.TeamRepo;
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
class TeamRepoTest {


    @Autowired
    private TeamRepo teamRepo;

    @BeforeEach
    void init() {
        teamRepo.deleteAll();
    }

    @Test
    void isEmptyInitially() {
        long count = teamRepo.count();
        assertThat(count).isEqualTo(0L);
    }

    @Test
    void saveOne() {
        Team team = new Team(0L, "georgetown", "Georgetown", "Hoyas", "GU","GU","GU", "Blue","Grey","xxx" ,"xxx",32L, LocalDateTime.now());
        assertThat(team.getId()).isEqualTo(0L);
        team = teamRepo.saveAndFlush(team);
        assertThat(team.getId()).isGreaterThan(0L);
        assertThat(teamRepo.count()).isEqualTo(1);
    }

    @Test
    void saveMany() {
        List<Team> teams = IntStream.range(1, 35).mapToObj(n -> new Team(0L, "georgetown"+n, "Georgetown"+n, "Hoyas", "GU"+n,"GU","GU", "Blue","Grey","xxx" ,"xxx",32L, LocalDateTime.now())).toList();
        assertThat(teams).allMatch(c -> c.getId() == 0);
        teams = teamRepo.saveAllAndFlush(teams);
        assertThat(teams).allMatch(c -> c.getId() > 0);
        assertThat(teamRepo.count()).isEqualTo(34);
    }
}