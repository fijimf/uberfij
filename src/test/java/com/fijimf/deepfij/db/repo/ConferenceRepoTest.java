package com.fijimf.deepfij.db.repo;


import com.fijimf.deepfij.db.model.schedule.Conference;
import com.fijimf.deepfij.db.repo.schedule.ConferenceRepo;
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
class ConferenceRepoTest {


    @Autowired
    private ConferenceRepo conferenceRepo;

    @BeforeEach
    void init() {
        conferenceRepo.deleteAll();
    }

    @Test
    void isEmptyInitially() {
        long count = conferenceRepo.count();
        assertThat(count).isEqualTo(0L);
    }

    @Test
    void saveOne() {
        Conference conference = new Conference(0L, "big-east", "Big East", "Big East Conference", "www.balh balh blah", "zzz-xx-c-", 32L, LocalDateTime.now());
        assertThat(conference.getId()).isEqualTo(0L);
        conference = conferenceRepo.saveAndFlush(conference);
        assertThat(conference.getId()).isGreaterThan(0L);
        assertThat(conferenceRepo.count()).isEqualTo(1);
    }

    @Test
    void saveMany() {
        List<Conference> conferences = IntStream.range(1, 35).mapToObj(n -> new Conference(0L, RandomStringUtils.randomAlphabetic(12), "Conference " + n, "Conference " + n, "www.balh balh blah", "zzz-xx-c-"+n, 32L, LocalDateTime.now())).toList();
        assertThat(conferences).allMatch(c -> c.getId() == 0);
        conferences = conferenceRepo.saveAllAndFlush(conferences);
        assertThat(conferences).allMatch(c -> c.getId() > 0);
        assertThat(conferenceRepo.count()).isEqualTo(34);
    }
}