package com.fijimf.deepfij.services.api;

import com.fijimf.deepfij.db.model.quote.Quote;
import com.fijimf.deepfij.db.model.schedule.Game;
import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.repo.quote.QuoteRepo;
import com.fijimf.deepfij.model.GameSnapshot;
import com.fijimf.deepfij.scraping.SeasonManager;
import com.fijimf.deepfij.services.schedule.GameManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
public class ApiController {

    private final QuoteRepo quoteRepo;
    private final GameManager gameManager;
    private final SeasonManager seasonManager;

    public ApiController(QuoteRepo quoteRepo, GameManager gameManager, SeasonManager seasonManager) {
        this.quoteRepo = quoteRepo;
        this.gameManager = gameManager;
        this.seasonManager = seasonManager;
    }

    @GetMapping("/quote")
    public Quote getQuote(@RequestParam(required = false) String tag) {
        if (StringUtils.isNotBlank(tag)) {
            return quoteRepo.getRandomQuote(tag).orElseThrow();
        } else {
            return quoteRepo.getRandomQuote().orElseThrow();
        }
    }

    @GetMapping("/gameSnapshot/{id}")
    public GameSnapshot getGameSnapshot(@PathVariable Long id) {

        return gameManager.getGameSnapshot(id).orElseThrow();

    }

    @GetMapping("/season")
    @Cacheable("rest")
   public List<Map<String, Object>> getSeasons() {
        return seasonManager.findAllSeasons().stream().map(s ->
                Map.of("id", s.getId(),
                        "year", s.getSeason(),
                        "games", s.getGames().size(),
                        "teams", s.numTeams(),
                        "conferences", s.numConferences(),
                        "firstGame", s.firstGameDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                        "lastGame", s.lastGameDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                        "lastUpdated", (Object) seasonManager.findLastUpdate(s.getSeason()).orElse(s.defaultStartDate().atStartOfDay()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )

        ).toList();
    }

    @GetMapping("/season/{yyyy}")
    @Cacheable("rest")
    public List<ScheduleMonth> getSeason(@PathVariable int yyyy) {
        final String MONTH_FORMAT_PATTERN = "MMMM";
        Season season = seasonManager.findSeasonBySeason(yyyy);
        List<ScheduleMonth> scheduleMonths = new ArrayList<>();

        for (int m = 0; m < 6; m++) {
            LocalDate monthStart = calculateMonthStart(yyyy, m);
            List<ScheduleDate> gamesCount = calculateGamesCount(season, monthStart);
            scheduleMonths.add(new ScheduleMonth(monthStart.format(DateTimeFormatter.ofPattern(MONTH_FORMAT_PATTERN)), monthStart.getYear(), 1 + (monthStart.getDayOfWeek().ordinal() + 1) % 7, gamesCount));
        }
        return scheduleMonths;
    }

    private LocalDate calculateMonthStart(int yyyy, int monthOffset) {
        return (11 + monthOffset) > 12 ? LocalDate.of(yyyy, 11 + monthOffset - 12, 1) : LocalDate.of(yyyy - 1, 11 + monthOffset, 1);
    }

    private List<ScheduleDate> calculateGamesCount(Season season, LocalDate monthStart) {
        List<ScheduleDate> gamesCount = new ArrayList<>();
        for (int p = 0; p < monthStart.getMonth().length(monthStart.isLeapYear()); p++) {
            LocalDate day = monthStart.plusDays(p);
            gamesCount.add(new ScheduleDate(p + 1, season.getGamesForDate(day).size(), day.format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
        }
        return gamesCount;
    }


    @GetMapping("/season/{yyyy}/{yyyymmdd}")
    @Cacheable("rest")
    public Map<String, Object> getSeason(@PathVariable int yyyy, @PathVariable String yyyymmdd) {
        Season season = seasonManager.findSeasonBySeason(yyyy);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate d = LocalDate.parse(yyyymmdd, fmt);
        List<Map<String, String>> games = season.getGamesForDate(d).stream().map(this::gameDigest).toList();
        return Map.of("prettyDate", d.format(DateTimeFormatter.ofPattern("d-MMMM-yyyy")),
                "prev", d.minusDays(1).format(fmt),
                "next", d.plusDays(1).format(fmt),
                "games", games);
    }

    private Map<String, String> gameDigest(Game g) {
        Map<String, String> gameDigest = new HashMap<>();
        gameDigest.put("homeId", Long.toString(g.getHomeTeam().getId()));
        gameDigest.put("homeName", g.getHomeTeam().getName());
        gameDigest.put("homeLogo", g.getHomeTeam().getLogoUrl());
        gameDigest.put("homeColor", g.getHomeTeam().getColor());
        if (g.isComplete()) {
            gameDigest.put("homeScore", g.getHomeScore().toString());
            gameDigest.put("awayScore", g.getAwayScore().toString());
        }
        gameDigest.put("awayId", Long.toString(g.getAwayTeam().getId()));
        gameDigest.put("awayName", g.getAwayTeam().getName());
        gameDigest.put("awayLogo", g.getAwayTeam().getLogoUrl());
        gameDigest.put("awayColor", g.getAwayTeam().getColor());
        gameDigest.put("date", g.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        gameDigest.put("id", Long.toString(g.getId()));
        if (g.getLine() != null) {
            gameDigest.put("line", g.getLine());

            gameDigest.put("result", g.getLineResult());
        }
        if (g.getOverUnder() != null) {
            gameDigest.put("overUnder", g.getOverUnder().toString());
            gameDigest.put("overUnderResult", g.getOverUnderResult());
        }
        return gameDigest;
    }

    private record ScheduleMonth(String month, int year, int firstOfMonth, List<ScheduleDate> dates) {

    }

    private record ScheduleDate(int day, int games, String dateKey) {

    }
}
