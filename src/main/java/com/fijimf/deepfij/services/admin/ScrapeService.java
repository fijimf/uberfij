package com.fijimf.deepfij.services.admin;

import com.fijimf.deepfij.db.repo.schedule.ConferenceRepo;
import com.fijimf.deepfij.db.repo.schedule.TeamRepo;
import com.fijimf.deepfij.db.repo.scrape.EspnConferencesScrapeRepo;
import com.fijimf.deepfij.db.repo.scrape.EspnTeamsScrapeRepo;
import com.fijimf.deepfij.scraping.ConferencesScrapeManager;
import com.fijimf.deepfij.scraping.TeamsScrapeManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/scrape")
public class ScrapeService {

    private final ConferenceRepo conferenceRepo;
    private final EspnConferencesScrapeRepo conferencesScrapeRepo;
    private final ConferencesScrapeManager conferencesScrapeManager;
    private final TeamRepo teamRepo;
    private final EspnTeamsScrapeRepo teamsScrapeRepo;
    private final TeamsScrapeManager teamsScrapeManager;

    public ScrapeService(ConferenceRepo conferenceRepo, EspnConferencesScrapeRepo conferencesScrapeRepo, ConferencesScrapeManager conferencesScrapeManager, TeamRepo teamRepo, EspnTeamsScrapeRepo teamsScrapeRepo, TeamsScrapeManager teamsScrapeManager) {
        this.conferenceRepo = conferenceRepo;
        this.conferencesScrapeRepo = conferencesScrapeRepo;
        this.conferencesScrapeManager = conferencesScrapeManager;
        this.teamRepo = teamRepo;
        this.teamsScrapeRepo = teamsScrapeRepo;
        this.teamsScrapeManager = teamsScrapeManager;
    }

    @GetMapping("/conferences/status")
    public ModelAndView getConferencesStatus() {
        ModelAndView modelAndView = new ModelAndView("scrape/conferencesStatus");
        modelAndView.addObject("confs", conferenceRepo.findAll());
        modelAndView.addObject("confScrapes", conferencesScrapeRepo.findAll());
        conferencesScrapeRepo.findAll();
        return modelAndView;
    }

    @GetMapping("/conferences/scrape")
    public ModelAndView scrapeConferences() {
        conferencesScrapeManager.scrape();
        return new ModelAndView("redirect:/admin/scrape/conferences/status");
    }

    @GetMapping(value = "/conferences/raw/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String showRawConferencesScrape(@PathVariable long id) {
        return conferencesScrapeManager.showRawConferencesScrape(id);
    }

    @GetMapping("/conferences/publishUpdate/{id}")
    public ModelAndView publishConferencesUpdate(@PathVariable long id) {
        conferencesScrapeManager.publishConferences(id, false);
        return new ModelAndView("redirect:/admin/scrape/conferences/status");
    }

    @GetMapping("/conferences/publishReplace/{id}")
    public ModelAndView publishConferencesReplace(@PathVariable long id) {
        conferencesScrapeManager.publishConferences(id, true);
        return new ModelAndView("redirect:/admin/scrape/conferences/status");
    }

    @GetMapping("/teams/status")
    public ModelAndView getTeamsStatus() {
        ModelAndView modelAndView = new ModelAndView("scrape/teamsStatus");
        modelAndView.addObject("teams", teamRepo.findAll());
        modelAndView.addObject("teamsScrapes", teamsScrapeRepo.findAll());
        return modelAndView;
    }
    @GetMapping("/teams/scrape")
    public ModelAndView scrapeTeams() {
        teamsScrapeManager.scrape();
        return new ModelAndView("redirect:/admin/scrape/teams/status");
    }

    @GetMapping(value = "/teams/raw/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String showRawTeamsScrape(@PathVariable long id) {
        return teamsScrapeManager.showRawConferencesScrape(id);
    }

    @GetMapping("/teams/publishUpdate/{id}")
    public ModelAndView publishTeamsUpdate(@PathVariable long id) {
        teamsScrapeManager.publishTeams(id, false);
        return new ModelAndView("redirect:/admin/scrape/teams/status");
    }

    @GetMapping("/teams/publishReplace/{id}")
    public ModelAndView publishTeamsReplace(@PathVariable long id) {
        teamsScrapeManager.publishTeams(id, true);
        return new ModelAndView("redirect:/admin/scrape/teams/status");
    }
}
