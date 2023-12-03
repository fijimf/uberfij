package com.fijimf.deepfij.services.schedule;

import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.repo.schedule.SeasonRepo;
import com.fijimf.deepfij.db.repo.schedule.TeamRepo;
import com.fijimf.deepfij.model.TeamPage;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TeamManager {
    private final TeamRepo teamRepo;

    private final SeasonRepo seasonRepo;

    public TeamManager(TeamRepo teamRepo, SeasonRepo seasonRepo) {
        this.teamRepo = teamRepo;
        this.seasonRepo = seasonRepo;
    }

    @Cacheable("teamPage")
    public Optional<TeamPage> loadTeamPage(String key, Integer year) {
        List<Season> seasons = seasonRepo.findAll();
        return teamRepo.findByKey(key).map(t -> year == null ? new TeamPage(t, seasons) : new TeamPage(year, t, seasons));
    }
}
