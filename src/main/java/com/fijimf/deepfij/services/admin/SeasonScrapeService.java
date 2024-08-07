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
        modelAndView.addObject("standingsScrapes", seasonMgr.findStandingsScrapesBySeason(seasonMgr.findById(id).getSeason()));
        modelAndView.addObject("season", seasonMgr.findById(id));
        modelAndView.addObject("seasonScrapes", seasonMgr.findSeasonScrapesBySeason(seasonMgr.findById(id)));
        return modelAndView;
    }

    @PostMapping("/new")
    public ModelAndView createNewSeason(@RequestParam(name = "seasonYear") String year) {
        seasonMgr.createNewSeason(Integer.parseInt(year));
        return new ModelAndView("forward:/admin/scrape/index");
    }

    @PostMapping("/updateCurrent")
    public ModelAndView updateCurrentSeason() {
        seasonMgr.updateCurrentSeason();
        return new ModelAndView("forward:/admin/scrape/index");
    }

    @GetMapping("/conferenceMappings/scrape/{year}")
    public ModelAndView scrapeConferenceMapping(@PathVariable("year") int year) {
        EspnStandingsScrape standingsScrape = standingsMgr.scrape(year);
        Season season = seasonMgr.findSeasonBySeason(standingsScrape.getSeason());
        return new ModelAndView("forward:/admin/scrape/seasons/index/" + season.getId());
    }

    @GetMapping("/games/scrape/{year}")
    public ModelAndView scrapeGames(@PathVariable("year") int year, @RequestParam(name = "from", required = false) String from, @RequestParam(name = "to", required = false) String to, @RequestParam(name = "timeOutSec", required = false) String timeOutSec) {
        Season season = seasonMgr.findSeasonBySeason(year);
        Long seasonScrapeId = seasonMgr.scrapeSeasonByYear(year, from, to, timeOutSec);
        return new ModelAndView("redirect:/admin/scrape/seasons/detail/" + season.getId() + "/" + seasonScrapeId);
    }

    @GetMapping(value = "/conferenceMappings/raw/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String showRawConferenceMappingScrape(@PathVariable long id) {
        return standingsMgr.showRawStandingsScrape(id);
    }

    @GetMapping("/conferenceMappings/publishUpdate/{id}")
    public ModelAndView publishConferenceMappingUpdate(@PathVariable long id) {
        Season season = standingsMgr.publishConferenceMap(id, false);
        return new ModelAndView("forward:/admin/scrape/seasons/index/" + season.getId());
    }

    @GetMapping("/conferenceMappings/publishReplace/{id}")
    public ModelAndView publishConferenceMappingReplace(@PathVariable long id) {
        Season season = standingsMgr.publishConferenceMap(id, true);
        return new ModelAndView("forward:/admin/scrape/seasons/index/" + season.getId());
    }

    @GetMapping("/cancel/{seasonId}/{id}")
    public ModelAndView cancelSeasonScrape(@PathVariable long seasonId, @PathVariable long id) {
        seasonMgr.cancelSeasonScrape(id);
        return new ModelAndView("redirect:/admin/scrape/seasons/index/" + seasonId);
    }

    @GetMapping("/publish/{seasonId}/{id}")
    public ModelAndView publishSeasonScrape(@PathVariable long seasonId, @PathVariable long id) {
        Season season = seasonMgr.findById(seasonId);
        seasonMgr.publishSeasonScrape(id, season);
        return new ModelAndView("forward:/admin/scrape/seasons/index/" + seasonId);
    }

    @GetMapping("/detail/{seasonId}/{id}")
    public ModelAndView showSeasonScrapeDetails(@PathVariable long seasonId, @PathVariable long id) {
        ModelAndView modelAndView = new ModelAndView("scrape/seasons/scrapeDetail");
        modelAndView.addObject("season", seasonMgr.findById(seasonId));
        modelAndView.addObject("seasonScrape", seasonMgr.findSeasonScrapeById(id));
        return modelAndView;
    }

    @GetMapping("/publishScoreboard/{id}")
    public ModelAndView publishScoreboardScrape(@PathVariable long id) {
        seasonMgr.publishScoreboardScrape(id);
        return new ModelAndView("redirect:");
    }
}
