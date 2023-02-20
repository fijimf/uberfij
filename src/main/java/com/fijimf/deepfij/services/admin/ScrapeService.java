package com.fijimf.deepfij.services.admin;

import com.fijimf.deepfij.scraping.ConferencesScrapeManager;
import com.fijimf.deepfij.scraping.SeasonManager;
import com.fijimf.deepfij.scraping.TeamsScrapeManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/scrape")
public class ScrapeService {

    private final ConferencesScrapeManager conferenceMgr;
    private final TeamsScrapeManager teamMgr;
    private final SeasonManager seasonMgr;

    public ScrapeService(ConferencesScrapeManager conferenceMgr, TeamsScrapeManager teamMgr, SeasonManager seasonMgr) {
        this.conferenceMgr = conferenceMgr;
        this.teamMgr = teamMgr;
        this.seasonMgr = seasonMgr;
    }

    @GetMapping("/index")
    public ModelAndView showOverallStatus() {
        ModelAndView modelAndView = new ModelAndView("scrape/status");
        modelAndView.addObject("conferences", conferenceMgr.findAllConferences());
        modelAndView.addObject("teams", teamMgr.findAllTeams());
        modelAndView.addObject("seasons", seasonMgr.findAllSeasons());
        return modelAndView;
    }

}
