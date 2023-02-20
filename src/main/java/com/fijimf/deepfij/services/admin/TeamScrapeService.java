package com.fijimf.deepfij.services.admin;

import com.fijimf.deepfij.scraping.TeamsScrapeManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/scrape/teams")
public class TeamScrapeService {

    private final TeamsScrapeManager teamMgr;

    public TeamScrapeService(TeamsScrapeManager teamMgr) {
        this.teamMgr = teamMgr;
    }

    @GetMapping("/index")
    public ModelAndView getTeamsStatus() {
        ModelAndView modelAndView = new ModelAndView("scrape/teamsStatus");
        modelAndView.addObject("teams", teamMgr.findAllTeams());
        modelAndView.addObject("teamsScrapes", teamMgr.findAllTeamScrapes());
        return modelAndView;
    }

    @GetMapping("/scrape")
    public ModelAndView scrapeTeams() {
        teamMgr.scrape();
        return new ModelAndView("redirect:/admin/scrape/teams/status");
    }

    @GetMapping(value = "/raw/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String showRawTeamsScrape(@PathVariable long id) {
        return teamMgr.showRawConferencesScrape(id);
    }

    @GetMapping("/publishUpdate/{id}")
    public ModelAndView publishTeamsUpdate(@PathVariable long id) {
        teamMgr.publishTeams(id, false);
        return new ModelAndView("redirect:/admin/scrape/teams/status");
    }

    @GetMapping("/publishReplace/{id}")
    public ModelAndView publishTeamsReplace(@PathVariable long id) {
        teamMgr.publishTeams(id, true);
        return new ModelAndView("redirect:/admin/scrape/teams/status");
    }
}
