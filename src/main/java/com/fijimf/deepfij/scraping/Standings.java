package com.fijimf.deepfij.scraping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Standings {

    public static final Logger logger = LoggerFactory.getLogger(Standings.class);
    private String shortName;
    private StandingsConference[] children;

    public Standings() {
    }

    public Standings(String shortName, StandingsConference[] children) {
        this.shortName = shortName;
        this.children = children;
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

    public Map<String, List<String>> mapValues() {
        return Arrays.stream(children)
                .peek(c->logger.info(c.getName()+"["+c.getId()+"]"))
                .collect(Collectors.toMap(
                        StandingsConference::getId,
                        c -> c.consolidatedStandings()
                                .stream()
                                .peek(e->logger.info(" -> "+e.getTeam().getShortDisplayName()+"["+e.getTeam().getId()+"]"))
                                .map(e -> e.getTeam().getId())
                                .collect(Collectors.toList())
                ));
    }
}
