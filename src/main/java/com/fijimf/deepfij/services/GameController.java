package com.fijimf.deepfij.services;

import com.fijimf.deepfij.model.GamesPage;
import com.fijimf.deepfij.services.schedule.GameManager;
import com.fijimf.deepfij.services.schedule.TeamManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller

public class GameController {
    private final GameManager gameManager;

    public GameController(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @GetMapping("/games/{yyyymmdd}")
    public ModelAndView games(@PathVariable(value = "yyyymmdd", required = false) final String yyyymmdd) {
        GamesPage gamesPage = gameManager.getGamesPage(yyyymmdd);
        return new ModelAndView("games_by_date.html", "page", gamesPage);
    }
}
