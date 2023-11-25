package com.fijimf.deepfij.db.repo;

import com.fijimf.deepfij.db.model.schedule.Conference;
import com.fijimf.deepfij.db.model.schedule.ConferenceMap;
import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.schedule.Team;
import com.fijimf.deepfij.db.repo.schedule.ConferenceMappingRepo;
import com.fijimf.deepfij.db.repo.schedule.ConferenceRepo;
import com.fijimf.deepfij.db.repo.schedule.SeasonRepo;
import com.fijimf.deepfij.db.repo.schedule.TeamRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test-containers")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sql/insert_conference_mapping_test_data.sql")
public class ConferenceMappingRepoTest {

    @Autowired
    private ConferenceMappingRepo conferenceMappingRepo;
    @Autowired
    private SeasonRepo seasonRepo;
    @Autowired
    private TeamRepo teamRepo;
    @Autowired
    private ConferenceRepo conferenceRepo;

    @BeforeEach
    void setUp() {
        conferenceMappingRepo.deleteAll();
    }

    @Test
    void findBySeasonIdAndTeamId() {
        ConferenceMap conferenceMap = new ConferenceMap();
        Season season = seasonRepo.findBySeason(2023).orElseThrow();
        Team team = teamRepo.findByKey("georgetown").orElseThrow();
        Conference conference = conferenceRepo.findById(1L).orElseThrow();
        conferenceMap.setSeason(season);
        conferenceMap.setTeam(team);
        conferenceMap.setConference(conference);
        conferenceMap.setScrapeSrcId(0L);
        conferenceMap.setPublishedAt(LocalDateTime.now());
        conferenceMappingRepo.saveAndFlush(conferenceMap);
        Optional<ConferenceMap> result = conferenceMappingRepo.findBySeasonIdAndTeamId(season.getId(), team.getId());

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(conferenceMap);
    }

    @Test
    void findBySeasonAndTeam() {
        ConferenceMap conferenceMap = new ConferenceMap();
        Season season = seasonRepo.findBySeason(2023).orElseThrow();
        Team team = teamRepo.findByKey("georgetown").orElseThrow();
        Conference conference = conferenceRepo.findById(1L).orElseThrow();

        conferenceMap.setSeason(season);
        conferenceMap.setTeam(team);
        conferenceMap.setConference(conference);
        conferenceMap.setScrapeSrcId(0L);
        conferenceMap.setPublishedAt(LocalDateTime.now());
        conferenceMappingRepo.saveAndFlush(conferenceMap);

        Optional<ConferenceMap> result = conferenceMappingRepo.findBySeasonAndTeam(season, team);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(conferenceMap);
    }

    @Test
    void deleteBySeasonIdAndScrapeSrcIdNot() {

        Season season1 = seasonRepo.findBySeason(2023).orElseThrow();
                Season season2 = seasonRepo.findBySeason(2015).orElseThrow();

        Team team = teamRepo.findByKey("georgetown").orElseThrow();
        Conference conference = conferenceRepo.findById(1L).orElseThrow();

        ConferenceMap conferenceMap1 = new ConferenceMap();
        conferenceMap1.setSeason(season1);
        conferenceMap1.setTeam(team);
        conferenceMap1.setConference(conference);
        conferenceMap1.setScrapeSrcId(0L);
        conferenceMap1.setPublishedAt(LocalDateTime.now());
        conferenceMappingRepo.saveAndFlush(conferenceMap1);

        ConferenceMap conferenceMap2 = new ConferenceMap();
        conferenceMap2.setSeason(season2);
        conferenceMap2.setTeam(team);
        conferenceMap2.setConference(conference);
        conferenceMap2.setScrapeSrcId(123L);
        conferenceMap2.setPublishedAt(LocalDateTime.now());
        conferenceMappingRepo.saveAndFlush(conferenceMap2);

        conferenceMappingRepo.deleteBySeasonIdAndScrapeSrcIdNot(season1.getId(), 123L);

        assertThat(conferenceMappingRepo.findAll()).containsExactly(conferenceMap2);
    }
}