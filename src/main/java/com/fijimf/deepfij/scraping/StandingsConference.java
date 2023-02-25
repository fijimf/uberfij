package com.fijimf.deepfij.scraping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StandingsConference {
    private final static Logger logger = LoggerFactory.getLogger(StandingsConference.class);
    private String id;
    private String name;
    private String abbreviation;
    private String shortName;

    private StandingsConference[] children;

    private ConferenceStandings standings;


    public StandingsConference() {
    }

    public StandingsConference(String id, String name, String abbreviation, String shortName, StandingsConference[]  children, ConferenceStandings standings) {
        this.id = id;
        this.name = name;
        this.abbreviation = abbreviation;
        this.shortName = shortName;
        this.children = children;
        this.standings = standings;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public StandingsConference[] getChildren() {
        return children;
    }

    public void setChildren(StandingsConference[] children) {
        this.children = children;
    }

    public ConferenceStandings getStandings() {
        return standings;
    }

    public void setStandings(ConferenceStandings standings) {
        this.standings = standings;
    }

    public List<StandingsLine> consolidatedStandings() {
        if (standings==null && children!=null){
            return Arrays.stream(children).flatMap(c->c.consolidatedStandings().stream()).collect(Collectors.toList());
        } else if (standings!=null && children==null){
            return Arrays.stream(standings.getEntries()).toList();
        } else {
            logger.warn("Conference "+getName()+" has no teams");
            return Collections.emptyList();
        }
    }
}
