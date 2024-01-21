package com.fijimf.deepfij.scraping;

import com.fijimf.deepfij.db.model.schedule.Conference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConferencesTest {

    private Conferences conferences;

    @BeforeEach
    public void setUp() {
        ConferencesConference conference1 = mock(ConferencesConference.class);
        when(conference1.getConference()).thenReturn(new Conference(1L,"conf-1","Conference 1","Conference 1","http://logo/1","111",1L, LocalDateTime.now()));

        ConferencesConference conference2 = mock(ConferencesConference.class);
        when(conference2.getConference()).thenReturn(new Conference(2L,"all","All","All","null","123",1L, LocalDateTime.now()));

        ConferencesConference[] conferencesArray = new ConferencesConference[]{conference1, conference2};
        conferences = new Conferences(conferencesArray);
    }

    @Test
    public void testValues() {
        List<Conference> validConferences = conferences.values();
        assertEquals(1, validConferences.size());

        Conference conference = validConferences.get(0);
        assertEquals("Conference 1", conference.getName());
        assertTrue(conference.isValid());
    }
}
