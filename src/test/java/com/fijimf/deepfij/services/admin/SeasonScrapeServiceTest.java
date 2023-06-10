package com.fijimf.deepfij.services.admin;

import com.fijimf.deepfij.db.model.schedule.ConferenceMap;
import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.scrape.EspnStandingsScrape;
import com.fijimf.deepfij.scraping.SeasonManager;
import com.fijimf.deepfij.scraping.StandingsScrapeManager;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SeasonScrapeServiceTest {

    @Mock
    private StandingsScrapeManager standingsScrapeManager;

    @Mock
    private SeasonManager seasonManager;

    @InjectMocks
    private SeasonScrapeService seasonScrapeService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        seasonScrapeService = new SeasonScrapeService(standingsScrapeManager, seasonManager);
    }

    @Test
    public void testGetSeasonsStatus() {
        Long id = 1L;
        Season season = new Season();
        season.setId(id);
        when(seasonManager.findById(id)).thenReturn(season);
        ModelAndView modelAndView = seasonScrapeService.getSeasonsStatus(id);
        assert (modelAndView.getViewName()).equals("scrape/seasons/index");
        assert (modelAndView.getModelMap()).containsKey("standingsScrapes");
        assert (modelAndView.getModelMap()).containsKey("season");
        assert (modelAndView.getModelMap()).containsKey("seasonScrapes");
    }

    @Test
    public void testCreateNewSeason() {
        // Arrange
        String year = "2022";
        doNothing().when(seasonManager).createNewSeason(Integer.parseInt(year));

        // Act
        ModelAndView modelAndView = seasonScrapeService.createNewSeason(year);

        // Assert
        verify(seasonManager, times(1)).createNewSeason(Integer.parseInt(year));
        assertEquals(modelAndView.getViewName(), "forward:/admin/scrape/index");
    }

    @Test
    public void testGetConferenceMappingStatus() {
        // Arrange
        ModelAndView expectedModelAndView = new ModelAndView("scrape/conferenceMapsStatus");
        List<ConferenceMap> mappings = Arrays.asList(new ConferenceMap(), new ConferenceMap());
        List<EspnStandingsScrape> standingsScrapes = Arrays.asList(new EspnStandingsScrape(), new EspnStandingsScrape());
        when(standingsScrapeManager.findAllConfMaps()).thenReturn(mappings);
        when(standingsScrapeManager.findAllStandingsScrapes()).thenReturn(standingsScrapes);

        // Act
        ModelAndView actualModelAndView = seasonScrapeService.getConferenceMappingStatus();

        // Assert
        verify(standingsScrapeManager, times(1)).findAllConfMaps();
        verify(standingsScrapeManager, times(1)).findAllStandingsScrapes();
     //   assertEquals(expectedModelAndView.getViewName(), actualModelAndView.getViewName());
     //   assertEquals(expectedModelAndView.getModel().get("mappings"), actualModelAndView.getModel().get("mappings"));
   //     assertEquals(expectedModelAndView.getModel().get("standingsScrapes"), actualModelAndView.getModel().get("standingsScrapes"));
    }

    @Test
    public void testScrapeConferenceMapping() {
        // Arrange
        int year = 2022;
        EspnStandingsScrape espnStandingsScrape = new EspnStandingsScrape();
        espnStandingsScrape.setSeason(year);
        Season season = new Season();
        season.setId(1L);
        season.setSeason(year);
        when(standingsScrapeManager.scrape(year)).thenReturn(espnStandingsScrape);
        when(seasonManager.findSeasonBySeason(espnStandingsScrape.getSeason())).thenReturn(season);

        // Act
        ModelAndView modelAndView = seasonScrapeService.scrapeConferenceMapping(year);

        // Assert
        verify(standingsScrapeManager, times(1)).scrape(year);
        verify(seasonManager, times(1)).findSeasonBySeason(espnStandingsScrape.getSeason());
        assertEquals(modelAndView.getViewName(), "forward:/admin/scrape/seasons/index/" + season.getId());
    }

//    @Test
//    void getSeasonsStatus() {
//    }
//
//    @Test
//    void createNewSeason() {
//    }
//
//    @Test
//    void getConferenceMappingStatus() {
//    }
//
//    @Test
//    void scrapeConferenceMapping() {
//    }
//
//    @Test
//    void scrapeGames() {
//    }
//
//    @Test
//    void showRawConferenceMappingScrape() {
//    }
//
//    @Test
//    void publishConferenceMappingUpdate() {
//    }
//
//    @Test
//    void publishConferenceMappingReplace() {
//    }

    // Add more test methods here for other methods in SeasonScrapeService
}
