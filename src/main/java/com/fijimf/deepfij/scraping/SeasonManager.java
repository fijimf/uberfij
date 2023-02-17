package com.fijimf.deepfij.scraping;

import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.repo.schedule.SeasonRepo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeasonManager {

    private SeasonRepo repo;

    public SeasonManager(SeasonRepo repo) {
        this.repo = repo;
    }

    public List<Season> findAllSeasons() {
        return repo.findAll();
    }

    public void createNewSeason(int year) {
        repo.saveAndFlush(new Season(0L, year));
    }
}
