package com.fijimf.deepfij.services.admin;

import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.scrape.EspnStandingsScrape;
import com.fijimf.deepfij.scraping.SeasonManager;
import com.fijimf.deepfij.scraping.StandingsScrapeManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SeasonScrapeServiceTest {

    @Mock
    private StandingsScrapeManager standingsScrapeManager;

    @Mock
    private SeasonManager seasonManager;

    @InjectMocks
    private SeasonScrapeService seasonScrapeService;

    @BeforeEach
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
        assertThat(modelAndView.getViewName()).isEqualTo("scrape/seasons/index");
        assertThat (modelAndView.getModelMap()).containsKey("standingsScrapes");
        assertThat (modelAndView.getModelMap()).containsKey("season");
        assertThat (modelAndView.getModelMap()).containsKey("seasonScrapes");
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
        assertThat(modelAndView.getViewName()).isEqualTo( "forward:/admin/scrape/index");
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
        assertThat(modelAndView.getViewName()).isEqualTo( "forward:/admin/scrape/seasons/index/" + season.getId());
    }
}
