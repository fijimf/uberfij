package com.fijimf.deepfij.services.admin;

import com.fijimf.deepfij.db.repo.schedule.ConferenceMappingRepo;
import com.fijimf.deepfij.db.repo.schedule.SeasonRepo;
import com.fijimf.deepfij.db.repo.schedule.TeamRepo;
import com.fijimf.deepfij.db.repo.scrape.EspnStandingsScrapeRepo;
import com.fijimf.deepfij.db.repo.scrape.EspnTeamsScrapeRepo;
import com.fijimf.deepfij.scraping.ConferencesScrapeManager;
import com.fijimf.deepfij.scraping.SeasonManager;
import com.fijimf.deepfij.scraping.StandingsScrapeManager;
import com.fijimf.deepfij.scraping.TeamsScrapeManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/scrape")
public class ScrapeService {

    private final ConferencesScrapeManager confScrMgr;
    private final TeamsScrapeManager teamScrMgr;
    private final StandingsScrapeManager standingsScrapeManager;

    private final SeasonRepo seasonRepo;

    private final SeasonManager seasonManager;

    public ScrapeService(ConferencesScrapeManager confScrMgr, TeamRepo teamRepo, EspnTeamsScrapeRepo teamsScrapeRepo, TeamsScrapeManager teamScrMgr, ConferenceMappingRepo conferenceMappingRepo, EspnStandingsScrapeRepo standingsScrapeRepo, StandingsScrapeManager standingsScrapeManager, SeasonRepo seasonRepo, SeasonManager seasonManager) {

        this.confScrMgr = confScrMgr;
        this.teamScrMgr = teamScrMgr;
        this.standingsScrapeManager = standingsScrapeManager;
        this.seasonRepo = seasonRepo;
        this.seasonManager = seasonManager;
    }

    @GetMapping("/status")
    public ModelAndView showOverallStatus() {
        ModelAndView modelAndView = new ModelAndView("scrape/status");
        modelAndView.addObject("confs", confScrMgr.findAllConferences());
        modelAndView.addObject("teams", teamScrMgr.findAllTeams());
        modelAndView.addObject("seasons", seasonManager.findAllSeasons());
        return modelAndView;
    }
    @GetMapping("/seasons/status")
    public ModelAndView getSeasonsStatus() {
        ModelAndView modelAndView = new ModelAndView("scrape/seasonStatus");
        modelAndView.addObject("seasons", seasonManager.findAllSeasons());
        return modelAndView;
    }
    @PostMapping("/seasons/new")
    public ModelAndView createNewSeason(@RequestParam(name="seasonYear") String year) {
      seasonManager.createNewSeason(Integer.parseInt(year));
        return new ModelAndView("redirect:/admin/scrape/seasons/status");
    }

    @GetMapping("/conferences/status")
    public ModelAndView getConferencesStatus() {
        ModelAndView modelAndView = new ModelAndView("scrape/conferencesStatus");
        modelAndView.addObject("confs", confScrMgr.findAllConferences());
        modelAndView.addObject("confScrapes", confScrMgr.findAllConferenceScrapes());
        return modelAndView;
    }

    @GetMapping("/conferences/scrape")
    public ModelAndView scrapeConferences() {
        confScrMgr.scrape();
        return new ModelAndView("redirect:/admin/scrape/conferences/status");
    }

    @GetMapping(value = "/conferences/raw/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String showRawConferencesScrape(@PathVariable long id) {
        return confScrMgr.showRawConferencesScrape(id);
    }

    @GetMapping("/conferences/publishUpdate/{id}")
    public ModelAndView publishConferencesUpdate(@PathVariable long id) {
        confScrMgr.publishConferences(id, false);
        return new ModelAndView("redirect:/admin/scrape/conferences/status");
    }

    @GetMapping("/conferences/publishReplace/{id}")
    public ModelAndView publishConferencesReplace(@PathVariable long id) {
        confScrMgr.publishConferences(id, true);
        return new ModelAndView("redirect:/admin/scrape/conferences/status");
    }

    @GetMapping("/teams/status")
    public ModelAndView getTeamsStatus() {
        ModelAndView modelAndView = new ModelAndView("scrape/teamsStatus");
        modelAndView.addObject("teams", teamScrMgr.findAllTeams());
        modelAndView.addObject("teamsScrapes", teamScrMgr.findAllTeamScrapes());
        return modelAndView;
    }

    @GetMapping("/teams/scrape")
    public ModelAndView scrapeTeams() {
        teamScrMgr.scrape();
        return new ModelAndView("redirect:/admin/scrape/teams/status");
    }

    @GetMapping(value = "/teams/raw/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String showRawTeamsScrape(@PathVariable long id) {
        return teamScrMgr.showRawConferencesScrape(id);
    }

    @GetMapping("/teams/publishUpdate/{id}")
    public ModelAndView publishTeamsUpdate(@PathVariable long id) {
        teamScrMgr.publishTeams(id, false);
        return new ModelAndView("redirect:/admin/scrape/teams/status");
    }

    @GetMapping("/teams/publishReplace/{id}")
    public ModelAndView publishTeamsReplace(@PathVariable long id) {
        teamScrMgr.publishTeams(id, true);
        return new ModelAndView("redirect:/admin/scrape/teams/status");
    }

    @GetMapping("/conferenceMappings/status")
    public ModelAndView getConferenceMappingStatus() {
        ModelAndView modelAndView = new ModelAndView("scrape/conferenceMapsStatus");
        modelAndView.addObject("mappings", standingsScrapeManager.findAllConfMaps());
        modelAndView.addObject("standingsScrapes", standingsScrapeManager.findAllStandingsScrapes());
        return modelAndView;
    }

    @GetMapping("/conferenceMappings/scrape/{year}")
    public ModelAndView scrapeConferenceMapping(@PathVariable("year") int year) {
        standingsScrapeManager.scrape(year);
        return new ModelAndView("redirect:/admin/scrape/conferenceMappings/status");
    }

    @GetMapping(value = "/conferenceMappings/raw/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String showRawConferenceMappingScrape(@PathVariable long id) {
        return standingsScrapeManager.showRawStandingsScrape(id);
    }

    @GetMapping("/conferenceMappings/publishUpdate/{id}")
    public ModelAndView publishConferenceMappingUpdate(@PathVariable long id) {
        standingsScrapeManager.publishConferenceMap(id, false);
        return new ModelAndView("redirect:/admin/scrape/conferenceMappings/status");
    }

    @GetMapping("/conferenceMappings/publishReplace/{id}")
    public ModelAndView publishConferenceMappingReplace(@PathVariable long id) {
        standingsScrapeManager.publishConferenceMap(id, true);
        return new ModelAndView("redirect:/admin/scrape/conferenceMappings/status");
    }
}
