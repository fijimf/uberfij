package com.fijimf.deepfij.services.admin;

import com.fijimf.deepfij.db.repo.schedule.ConferenceRepo;
import com.fijimf.deepfij.db.repo.scrape.EspnConferencesScrapeRepo;
import com.fijimf.deepfij.scraping.ConferencesScrapeManager;
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

    public ScrapeService(ConferenceRepo conferenceRepo, EspnConferencesScrapeRepo conferencesScrapeRepo, ConferencesScrapeManager conferencesScrapeManager) {
        this.conferenceRepo = conferenceRepo;
        this.conferencesScrapeRepo = conferencesScrapeRepo;
        this.conferencesScrapeManager = conferencesScrapeManager;
    }

    @GetMapping("/status")
    public ModelAndView getScrapingStatus() {
        ModelAndView modelAndView = new ModelAndView("scrape/status");
        modelAndView.addObject("confs", conferenceRepo.findAll());
        modelAndView.addObject("confScrapes", conferencesScrapeRepo.findAll());
        conferencesScrapeRepo.findAll();
        return modelAndView;
    }

    @GetMapping("/conferences/scrape")
    public ModelAndView scrapeConferences() {
        conferencesScrapeManager.scrape();
        return new ModelAndView("redirect:/admin/scrape/status");
    }

    @GetMapping(value = "/conferences/raw/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String showRawConferencesScrape(@PathVariable long id) {
        return conferencesScrapeManager.showRawConferencesScrape(id);
    }

    @GetMapping("/conferences/publishUpdate/{id}")
    public ModelAndView publishConferenceUpdate(@PathVariable long id) {
        conferencesScrapeManager.publishConferences(id, false);
        return new ModelAndView("redirect:/admin/scrape/status");
    }

    @GetMapping("/conferences/publishReplace/{id}")
    public ModelAndView publishConferenceReplace(@PathVariable long id) {
        conferencesScrapeManager.publishConferences(id, true);
        return new ModelAndView("redirect:/admin/scrape/status");
    }
}
