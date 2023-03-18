package com.fijimf.deepfij.scraping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fijimf.deepfij.db.model.schedule.Conference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
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

    public Map<String, List<StandingsTeam>> mapValues() {
        return Arrays.stream(children)
                .peek(c->logger.info(c.getName()+"["+c.getId()+"]"))
                .collect(Collectors.toMap(
                        StandingsConference::getId,
                        c -> c.consolidatedStandings()
                                .stream()
                                .peek(e->logger.info(" -> "+e.getTeam().getShortDisplayName()+"["+e.getTeam().getId()+"]"))
                                .map(StandingsLine::getTeam)
                                .collect(Collectors.toList())
                ));
    }

    public Conference conferenceFromStandings(String espnId){
        return Arrays.stream(children)
                .filter(sc->sc.getId().equalsIgnoreCase(espnId))
                .map(sc->new Conference(0L,sc.getAbbreviation(),sc.getName(),sc.getShortName(),guessLogoUrl(sc),espnId,-1L, LocalDateTime.now()))
                .findFirst().orElseThrow();

    }

    public String guessLogoUrl(StandingsConference sc){
//        https://a.espncdn.com/i/teamlogos/ncaa_conf/500/independents.png
        String s = sc.getShortName().toLowerCase().trim().replace(' ','_');
        return "https://a.espncdn.com/i/teamlogos/ncaa_conf/500/"+s+".png";
    }
}
