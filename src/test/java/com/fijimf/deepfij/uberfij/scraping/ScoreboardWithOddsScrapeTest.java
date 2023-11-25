package com.fijimf.deepfij.uberfij.scraping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fijimf.deepfij.scraping.Conferences;
import com.fijimf.deepfij.scraping.Scoreboard;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import static org.assertj.core.api.Assertions.assertThat;

public class ScoreboardWithOddsScrapeTest {

    @Test
    void testParseJson() throws IOException {

        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("data/scoreboard_with_odds.json")) {
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            Scoreboard mapped = objectMapper.readValue(inputStream, Scoreboard.class);
            assertThat(mapped).isNotNull();

        }

    }
    @Test
    void testSerialize() throws IOException {

        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("data/conferences.json")) {
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            Conferences mapped = objectMapper.readValue(inputStream, Conferences.class);
            assertThat(mapped).isNotNull();
            assertThat(mapped.values()).hasSize(33);
            objectMapper.writerFor(Conferences.class).writeValueAsString(mapped);
        }

    }
}
