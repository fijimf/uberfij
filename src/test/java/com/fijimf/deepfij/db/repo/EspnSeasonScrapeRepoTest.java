package com.fijimf.deepfij.db.repo;

import com.fijimf.deepfij.db.model.scrape.EspnSeasonScrape;
import com.fijimf.deepfij.db.repo.scrape.EspnSeasonScrapeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test-containers")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EspnSeasonScrapeRepoTest {
    @Autowired
    private EspnSeasonScrapeRepo espnSeasonScrapeRepo;

    @BeforeEach
    void init() {
        espnSeasonScrapeRepo.deleteAll();
    }

    @Test
    void isEmptyInitially() {
        long count = espnSeasonScrapeRepo.count();
        assertThat(count).isEqualTo(0L);
    }

    @Test
    void createSeasonScrape() {
        EspnSeasonScrape espnSeasonScrape = espnSeasonScrapeRepo.saveAndFlush(
                new EspnSeasonScrape(0L, 2022, LocalDate.of(2012, 11, 1), LocalDate.of(2021, 12, 1), LocalDateTime.now().minusMinutes(5), null, "STARTING"));
        assertThat(espnSeasonScrape.getId()).isGreaterThan(0L);
        assertThat(espnSeasonScrape.getStartedAt()).isNotNull();
        assertThat(espnSeasonScrape.getCompletedAt()).isNull();
        long count = espnSeasonScrapeRepo.count();
        assertThat(count).isEqualTo(1L);
    }


    @Test
    void updateStatus() {
        EspnSeasonScrape espnSeasonScrape = espnSeasonScrapeRepo.saveAndFlush(
                new EspnSeasonScrape(0L, 2022, LocalDate.of(2012, 11, 1), LocalDate.of(2021, 12, 1), LocalDateTime.now().minusMinutes(5), null, "STARTING"));
        assertThat(espnSeasonScrape.getId()).isGreaterThan(0L);
        assertThat(espnSeasonScrape.getStatus()).isEqualTo("STARTING");
        int n = espnSeasonScrapeRepo.updateStatusById(espnSeasonScrape.getId(), "RUNNING");
        espnSeasonScrapeRepo.findAll().forEach(System.err::println);
        assertThat(n).isEqualTo(1);
        Optional<EspnSeasonScrape> optionalEspnSeasonScrape = espnSeasonScrapeRepo.findById(espnSeasonScrape.getId());

        assertThat(optionalEspnSeasonScrape).isNotEmpty();
        assertThat(optionalEspnSeasonScrape.get().getStatus()).isEqualTo("RUNNING");

    }

}
