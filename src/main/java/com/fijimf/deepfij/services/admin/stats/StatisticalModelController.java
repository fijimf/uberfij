package com.fijimf.deepfij.services.admin.stats;

import com.fijimf.deepfij.analytics.StatsManager;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/admin/stats")
public class StatisticalModelController {
    private final static Logger logger = LoggerFactory.getLogger(StatisticalModelController.class);
    private final StatsManager statsManager;


    public StatisticalModelController(StatsManager statsManager) {
        this.statsManager = statsManager;
    }

    @Transactional
    @GetMapping("/wonlost/calc")
    public void calculateWonLostStats(@RequestParam("season") Integer season, HttpServletResponse response) {
        statsManager.calculateWonLostStats(season);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}

