package com.fijimf.deepfij.config;

import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.scrape.EspnStandingsScrape;
import com.fijimf.deepfij.scraping.ConferencesScrapeManager;
import com.fijimf.deepfij.scraping.SeasonManager;
import com.fijimf.deepfij.scraping.StandingsScrapeManager;
import com.fijimf.deepfij.scraping.TeamsScrapeManager;
import com.fijimf.deepfij.services.Mailer;
import com.fijimf.deepfij.services.user.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@MockBeans({
        @MockBean(ConferencesScrapeManager.class),
        @MockBean(TeamsScrapeManager.class),
//        @MockBean(StandingsScrapeManager.class),
//        @MockBean(SeasonManager.class),
        @MockBean(UserManager.class)
})
@WebMvcTest()
//@EnableWebSecurity
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class SecurityConfigTest {


    public static final List<String> ADMIN_SCRAPE_ENDPOINTS = List.of(
            "/admin/scrape/index",
            "/admin/scrape/conferences/clear",
            "/admin/scrape/conferences/index",
            "/admin/scrape/conferences/scrape",
            "/admin/scrape/conferences/raw/100",
            // "/admin/scrape/conferences/view",
            "/admin/scrape/conferences/publishUpdate/100",
            "/admin/scrape/conferences/publishReplace/100",
//            "/admin/scrape/seasons/conferenceMappings/clear",
            "/admin/scrape/seasons/conferenceMappings/index",
            "/admin/scrape/seasons/conferenceMappings/scrape/2022",
            "/admin/scrape/seasons/conferenceMappings/raw/100",
            // "/admin/scrape/conferences/view",
            "/admin/scrape/seasons/conferenceMappings/publishUpdate/100",
            "/admin/scrape/seasons/conferenceMappings/publishReplace/100"
    );
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeasonManager seasonManager;

    @MockBean
    private StandingsScrapeManager standingsMgr;

    @MockBean
    private Mailer mailer;
    @BeforeEach
    public void setUp() {
        Season s = new Season(1L, 2022);
        Mockito.when(seasonManager.findSeasonBySeason(Mockito.anyInt()))
                .thenReturn(s);

        Mockito.when(standingsMgr.publishConferenceMap(Mockito.anyLong(),Mockito.anyBoolean()))
                .thenReturn(s);
        Mockito.when(standingsMgr.scrape(Mockito.anyInt()))
                .thenReturn(new EspnStandingsScrape(1L, 2022,"xxx", LocalDateTime.now(),23L,200,"xxx","XXX","XXX"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAdminRoleAdminAllow() throws Exception {

        ADMIN_SCRAPE_ENDPOINTS.forEach(e -> {
            try {
                mockMvc.perform(get(e)).andExpect(status().isOk());
            } catch (Exception ex) {
                fail("Endpoint->" + e, ex);
            }
        });
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUserRoleAdminDeny() throws Exception {

        ADMIN_SCRAPE_ENDPOINTS.forEach(e -> {
            try {
                mockMvc.perform(get(e)).andExpect(status().isForbidden());
            } catch (Exception ex) {
                fail(ex);
            }
        });
    }

    @Test
    public void testAnonymousAdminRedirectToLogin() throws Exception {
        ADMIN_SCRAPE_ENDPOINTS.forEach(e -> {
            try {
                mockMvc
                        .perform(get(e))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("http://localhost/login"));

                mockMvc
                        .perform(get(e).with(anonymous()))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("http://localhost/login"));
            } catch (Exception ex) {
                fail(e+" failed",ex);
            }
        });
    }

    @Test
    public void testStaticResourcesAlwaysAllow() throws Exception {
        mockMvc.perform(get("/css/deepfij.css")).andExpect(status().isOk());
        mockMvc.perform(get("/css/deepfij.css").with(anonymous()))
                .andExpect(status().isOk());
        mockMvc.perform(get("/img/deepfij.png")).andExpect(status().isOk());
        mockMvc.perform(get("/img/deepfij.png").with(anonymous()))
                .andExpect(status().isOk());
    }

    @Test
    public void testWebjarsAlwaysAllow() throws Exception {
        mockMvc.perform(get("/webjars/bootstrap-icons/1.10.3/font/bootstrap-icons.css")).andExpect(status().isOk());
        mockMvc.perform(get("/webjars/bootstrap-icons/1.10.3/font/bootstrap-icons.css").with(anonymous()))
                .andExpect(status().isOk());
    }
    @Test
    public void testLoginAlwaysAllow() throws Exception {
//        mockMvc.perform(post("/login")).andExpect(status().isOk());
//        mockMvc.perform(post("/login").with(anonymous()))
//                .andExpect(status().isOk());
    }
}

