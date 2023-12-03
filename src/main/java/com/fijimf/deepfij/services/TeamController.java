package com.fijimf.deepfij.services;

import com.fijimf.deepfij.services.schedule.TeamManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller

public class TeamController {
    private final TeamManager teamManager;

    public TeamController(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @GetMapping("/team/{key}")
    public ModelAndView team(@PathVariable("key") final String key, @RequestParam(value = "season", required = false) final Integer season)  {
        return teamManager.loadTeamPage(key, season)
                .map(t -> new ModelAndView("team.html", "team", t))
                .orElseGet(() -> new ModelAndView("team_not_found.html"));
    }
}
