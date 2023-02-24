package com.fijimf.deepfij.scraping;

import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.scrape.EspnStandingsScrape;
import com.fijimf.deepfij.db.repo.schedule.SeasonRepo;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeasonManager {

    private final SeasonRepo repo;
    private final StandingsScrapeManager standingsMgr;

    public SeasonManager(SeasonRepo repo, StandingsScrapeManager standingsMgr) {
        this.repo = repo;
        this.standingsMgr = standingsMgr;
    }

    public List<Season> findAllSeasons() {
        return repo.findAll(Sort.by("season"));
    }

    public Season findById(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public List<EspnStandingsScrape> findStandingsScrapesBySeason(int year){
       return standingsMgr.findStandingScrapesBySeasonYear( year);
    }

    public void createNewSeason(int year) {
        repo.saveAndFlush(new Season(0L, year));
    }

    public Season findSeasonBySeason(int season) {
        return repo.findBySeason(season).orElseThrow();
    }
}
