package com.fijimf.deepfij.uberfij.scraping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fijimf.deepfij.db.model.schedule.Conference;
import com.fijimf.deepfij.scraping.Conferences;
import com.fijimf.deepfij.scraping.ConferencesConference;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ConferenceScrapeTest {

    @Test
    void testParseJson() throws IOException {

        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("data/conferences.json")) {
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            Conferences mapped = objectMapper.readValue(inputStream, Conferences.class);
            assertThat(mapped).isNotNull();
            List<Conference> conferences = mapped.values();
            assertThat(conferences).hasSize(33);
            assertThat(conferences).allMatch(c-> StringUtils.isNotBlank(c.getLogoUrl())||c.getKey().equalsIgnoreCase("asun"));
            assertThat(conferences).allMatch(Conference::isValid);
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
    @Test
    void testParseJson2() throws IOException {

        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("data/conference.json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            ConferencesConference cc = objectMapper.readValue(inputStream, ConferencesConference.class);
            assertThat(cc).isNotNull();
            assertThat(cc).hasFieldOrPropertyWithValue( "name", "Atlantic 10 Conference");
            assertThat(cc).hasFieldOrPropertyWithValue( "shortName", "A 10");
            assertThat(cc).hasFieldOrPropertyWithValue( "groupId", "3");
            assertThat(cc).hasFieldOrPropertyWithValue( "logo", "https://a.espncdn.com/i/teamlogos/ncaa_conf/500/atlantic_10.png");
            assertThat(cc).hasFieldOrPropertyWithValue( "parentGroupId", "50");
        }
    }
}
