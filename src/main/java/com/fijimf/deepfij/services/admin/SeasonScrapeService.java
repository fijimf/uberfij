package com.fijimf.deepfij.services.admin;

import com.fijimf.deepfij.scraping.SeasonManager;
import com.fijimf.deepfij.scraping.StandingsScrapeManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/scrape")
public class SeasonScrapeService {

    private final StandingsScrapeManager standingsMgr;

    private final SeasonManager seasonMgr;

    public SeasonScrapeService(StandingsScrapeManager standingsMgr, SeasonManager seasonMgr) {

        this.standingsMgr = standingsMgr;
        this.seasonMgr = seasonMgr;
    }

    @GetMapping("/seasons/status")
    public ModelAndView getSeasonsStatus() {
        ModelAndView modelAndView = new ModelAndView("scrape/seasonStatus");
        modelAndView.addObject("seasons", seasonMgr.findAllSeasons());
        return modelAndView;
    }

    @PostMapping("/seasons/new")
    public ModelAndView createNewSeason(@RequestParam(name = "seasonYear") String year) {
        seasonMgr.createNewSeason(Integer.parseInt(year));
        return new ModelAndView("redirect:/admin/scrape/seasons/status");
    }


    @GetMapping("/conferenceMappings/status")
    public ModelAndView getConferenceMappingStatus() {
        ModelAndView modelAndView = new ModelAndView("scrape/conferenceMapsStatus");
        modelAndView.addObject("mappings", standingsMgr.findAllConfMaps());
        modelAndView.addObject("standingsScrapes", standingsMgr.findAllStandingsScrapes());
        return modelAndView;
    }

    @GetMapping("/conferenceMappings/scrape/{year}")
    public ModelAndView scrapeConferenceMapping(@PathVariable("year") int year) {
        standingsMgr.scrape(year);
        return new ModelAndView("redirect:/admin/scrape/conferenceMappings/status");
    }

    @GetMapping(value = "/conferenceMappings/raw/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String showRawConferenceMappingScrape(@PathVariable long id) {
        return standingsMgr.showRawStandingsScrape(id);
    }

    @GetMapping("/conferenceMappings/publishUpdate/{id}")
    public ModelAndView publishConferenceMappingUpdate(@PathVariable long id) {
        standingsMgr.publishConferenceMap(id, false);
        return new ModelAndView("redirect:/admin/scrape/conferenceMappings/status");
    }

    @GetMapping("/conferenceMappings/publishReplace/{id}")
    public ModelAndView publishConferenceMappingReplace(@PathVariable long id) {
        standingsMgr.publishConferenceMap(id, true);
        return new ModelAndView("redirect:/admin/scrape/conferenceMappings/status");
    }
}
