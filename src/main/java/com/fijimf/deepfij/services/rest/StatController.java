package com.fijimf.deepfij.services.rest;

import com.fijimf.deepfij.model.GameScatterData;
import com.fijimf.deepfij.services.schedule.TeamManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StatController {

private final TeamManager teamMgr;

    public StatController(TeamManager teamMgr) {
        this.teamMgr = teamMgr;
    }

    @GetMapping("/teamStats")
    public List<StatsObservation> loadTeamSeasonStat(@RequestParam("teamId") Long teamId,@RequestParam("season") Integer season, @RequestParam("statistic") String statistic) {
        return teamMgr.loadSeasonStat( teamId,  season, statistic);
    }

    @GetMapping("/teamGames")
    public GameScatterData loadTeamGames(@RequestParam("key") String key){
        return teamMgr.loadTeamGames(key);
    }
}
