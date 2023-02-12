package com.fijimf.deepfij.uberfij.scraping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fijimf.deepfij.scraping.Standings;
import com.fijimf.deepfij.scraping.StandingsConference;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ConferenceMapScrapeTest {

    @Test
    void testParseJson() throws IOException {

        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("data/standings.json")) {
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            Standings mapped = objectMapper.readValue(inputStream, Standings.class);
            assertThat(mapped).isNotNull();
            assertThat(mapped.mapValues()).hasSize(32);
        }

    }
    @Test
    void testSimpleConference() throws IOException {

        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("data/meac.json")) {
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            StandingsConference mapped = objectMapper.readValue(inputStream, StandingsConference.class);
            assertThat(mapped).isNotNull();
            assertThat(mapped.getName()).isEqualTo("Mid-Eastern Athletic Conference");
            assertThat(mapped.getStandings()).isNotNull();
            assertThat(mapped.getChildren()).isNull();
            assertThat(mapped.consolidatedStandings()).hasSize(11);
        }

    }
    @Test
    void testConferenceWithDivisions() throws IOException {

        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("data/mac.json")) {
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            StandingsConference mapped = objectMapper.readValue(inputStream, StandingsConference.class);
            assertThat(mapped).isNotNull();
            assertThat(mapped.getName()).isEqualTo("Mid-American Conference");
            assertThat(mapped.getStandings()).isNull();
            assertThat(mapped.getChildren()).isNotNull();
            assertThat(mapped.consolidatedStandings()).hasSize(12);

        }

    }


    @Test
    void testSerialize() throws IOException {

        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("data/standings.json")) {
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            Standings mapped = objectMapper.readValue(inputStream, Standings.class);
            assertThat(mapped).isNotNull();
            Map<String, List<String>> confMap = mapped.mapValues();
            String value = objectMapper.writerFor(Standings.class).writeValueAsString(mapped);
            System.err.println(value);
            Standings mapped2 = objectMapper.readValue(value, Standings.class);
            Map<String, List<String>> confMap2 = mapped2.mapValues();
            assertThat(confMap).isEqualTo(confMap2);

        }
    }
}
