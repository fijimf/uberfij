package com.fijimf.deepfij.services.admin;

import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.scrape.EspnStandingsScrape;
import com.fijimf.deepfij.scraping.SeasonManager;
import com.fijimf.deepfij.scraping.StandingsScrapeManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/scrape/seasons")
public class SeasonScrapeService {

    private final StandingsScrapeManager standingsMgr;

    private final SeasonManager seasonMgr;

    public SeasonScrapeService(StandingsScrapeManager standingsMgr, SeasonManager seasonMgr) {
        this.standingsMgr = standingsMgr;
        this.seasonMgr = seasonMgr;
    }

    @GetMapping("/index/{id}")
    public ModelAndView getSeasonsStatus(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView("scrape/seasons/index");
        Season season = seasonMgr.findById(id);
        modelAndView.addObject("standingsScrapes", seasonMgr.findStandingsScrapesBySeason(season.getSeason()));
        modelAndView.addObject("season", season);
        return modelAndView;
    }

    @PostMapping("/new")
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
        EspnStandingsScrape standingsScrape = standingsMgr.scrape(year);
        Season season = seasonMgr.findSeasonBySeason(standingsScrape.getSeason());
        return new ModelAndView("redirect:/admin/scrape/seasons/index/" + season.getId());
    }

    @GetMapping("/games/scrape/{year}")
    public ModelAndView scrapeGames(@PathVariable("year") int year) {
        seasonMgr.scrapeSeasonByYear(year);
        Season season = seasonMgr.findSeasonBySeason(year);
        return new ModelAndView("redirect:/admin/scrape/seasons/index/" + season.getId());
    }

    @GetMapping(value = "/conferenceMappings/raw/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String showRawConferenceMappingScrape(@PathVariable long id) {
        return standingsMgr.showRawStandingsScrape(id);
    }

    @GetMapping("/conferenceMappings/publishUpdate/{id}")
    public ModelAndView publishConferenceMappingUpdate(@PathVariable long id) {
        Season season = standingsMgr.publishConferenceMap(id, false);
        return new ModelAndView("redirect:/admin/scrape/seasons/index/" + season.getId());
    }

    @GetMapping("/conferenceMappings/publishReplace/{id}")
    public ModelAndView publishConferenceMappingReplace(@PathVariable long id) {
        Season season = standingsMgr.publishConferenceMap(id, true);
        return new ModelAndView("redirect:/admin/scrape/seasons/index/" + season.getId());
    }
}
