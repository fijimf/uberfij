package com.fijimf.deepfij.services.admin;

import com.fijimf.deepfij.scraping.ConferencesScrapeManager;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/scrape/conferences")
public class ConferenceScrapeService {

    private final ConferencesScrapeManager conferenceMgr;

    public ConferenceScrapeService(ConferencesScrapeManager conferenceMgr) {
        this.conferenceMgr = conferenceMgr;
    }

    @GetMapping("/index")
    public ModelAndView getConferencesStatus() {
        ModelAndView modelAndView = new ModelAndView("scrape/conferences/index");
        modelAndView.addObject("conferences", conferenceMgr.findAllConferences());
        modelAndView.addObject("confScrapes", conferenceMgr.findAllConferenceScrapes());
        return modelAndView;
    }

    @GetMapping("/view")
    public ModelAndView getViewConferences() {
        throw new NotImplementedException();
    }

    @GetMapping("/clear")
    public ModelAndView getClearConferences() {
        ModelAndView modelAndView = new ModelAndView("scrape/conferences/index");
        modelAndView.addObject("conferences", conferenceMgr.findAllConferences());
        modelAndView.addObject("confScrapes", conferenceMgr.findAllConferenceScrapes());
        return modelAndView;
    }

    @GetMapping("/scrape")
    public ModelAndView scrapeConferences() {
        conferenceMgr.scrape();
        return conferencesIndexRedirect();
    }

    @GetMapping(value = "/raw/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String showRawConferencesScrape(@PathVariable long id) {
        return conferenceMgr.showRawConferencesScrape(id);
    }

    @GetMapping("/publishUpdate/{id}")
    public ModelAndView publishConferencesUpdate(@PathVariable long id) {
        conferenceMgr.publishConferences(id, false);
        return conferencesIndexRedirect();
    }


    @GetMapping("/publishReplace/{id}")
    public ModelAndView publishConferencesReplace(@PathVariable long id) {
        conferenceMgr.publishConferences(id, true);
        return conferencesIndexRedirect();
    }

    public static ModelAndView conferencesIndexRedirect() {
        return new ModelAndView("redirect:/admin/scrape/conferences/index");
    }

}
