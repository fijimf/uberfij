package com.fijimf.deepfij.scraping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fijimf.deepfij.db.model.schedule.Conference;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Conferences {
    private ConferencesConference[] conferences;

    public Conferences() {
    }

    public Conferences(ConferencesConference[] conferences) {
        this.conferences = conferences;
    }

    public ConferencesConference[] getConferences() {
        return conferences;
    }

    public void setConferences(ConferencesConference[] conferences) {
        this.conferences = conferences;
    }

    public List<Conference> values() {
        List<Conference> conferenceStream = Arrays.stream(conferences).map(ConferencesConference::getConference).collect(Collectors.toList());
        return conferenceStream.stream().filter(Conference::isValid).collect(Collectors.toList());
    }
}
