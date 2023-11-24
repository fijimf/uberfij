package com.fijimf.deepfij.services.schedule;

import com.fijimf.deepfij.db.repo.schedule.SeasonRepo;
import com.fijimf.deepfij.db.repo.schedule.TeamRepo;
import com.fijimf.deepfij.model.TeamPage;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TeamManager {
    private final TeamRepo teamRepo;

    private final SeasonRepo seasonRepo;

    public TeamManager(TeamRepo teamRepo, SeasonRepo seasonRepo) {
        this.teamRepo = teamRepo;
        this.seasonRepo = seasonRepo;
    }

    public Optional<TeamPage> loadTeamPage(String key) {
        return teamRepo.findByKey(key).map(t -> new TeamPage(t, seasonRepo.findAll()));
    }

    public Optional<TeamPage> loadTeamPage(String key, int year) {
        return teamRepo.findByKey(key).map(t -> new TeamPage(year, t, seasonRepo.findAll()));
    }
}
