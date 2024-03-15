package com.fijimf.deepfij.services.schedule;

import com.fijimf.deepfij.db.model.schedule.Game;
import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.schedule.Team;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatistic;
import com.fijimf.deepfij.db.repo.schedule.SeasonRepo;
import com.fijimf.deepfij.db.repo.schedule.TeamRepo;
import com.fijimf.deepfij.db.repo.statistic.DailyTeamStatisticRepo;
import com.fijimf.deepfij.model.*;
import com.fijimf.deepfij.scraping.SeasonManager;
import com.fijimf.deepfij.services.rest.StatsObservation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GameManager {
    private final TeamRepo teamRepo;

    private final SeasonRepo seasonRepo;

    private final DailyTeamStatisticRepo statisticRepo;

    public GameManager(TeamRepo teamRepo, SeasonRepo seasonRepo, DailyTeamStatisticRepo statisticRepo) {
        this.teamRepo = teamRepo;
        this.seasonRepo = seasonRepo;
        this.statisticRepo = statisticRepo;
    }

    @Cacheable("teamPage")
    public Optional<TeamPage> loadTeamPage(String key, Integer year) {
        List<Season> seasons = seasonRepo.findAll();
        return teamRepo.findByKey(key).map(t -> year == null ? new TeamPage(t, seasons) : new TeamPage(year, t, seasons));
    }

    public GameScatterData loadTeamGames(String key) {
        return teamRepo.findByKey(key).map(
                team -> new GameScatterData(
                        team.getName(),
                        team.getColor(),
                        seasonRepo
                                .findAll()
                                .stream()
                                .flatMap(s -> s.getGames()
                                        .stream()
                                        .filter(g -> g.hasTeam(team))
                                        .filter(Game::isComplete)
                                        .map(g -> new GameScatterData.TeamGame(
                                                s.getSeason(),
                                                g.getDate().getYear(),
                                                g.getDate().getMonthValue(),
                                                g.getDate().getDayOfMonth(),
                                                g.getScore(team),
                                                g.getOppScore(team),
                                                g.getOpponent(team).getName(),
                                                g.getOpponent(team).getLogoUrl())
                                        )
                                ).toList()
                )).orElseThrow();
    }

    public List<StatsObservation> loadSeasonStat(Long teamId, Integer season, String statistic) {
        return teamRepo.findById(teamId).map(t -> {
            List<DailyTeamStatistic> stats = statisticRepo.findByTeamSeasonAndName(t, statistic, season);
            return stats.stream()
                    .map(s -> new StatsObservation(s.getSummary().getDate(), s.getValue(), null, null, null, null, null, null, null, null, null))
                    .collect(Collectors.toList());
        }).orElse(Collections.emptyList());
    }

    public List<Team> loadTeamList() {
        return teamRepo.findAll(Sort.by("name"));
    }

    public LocalDate getDate(String yyyymmdd, LocalDate today, Map<Integer, Season> seasons) {
        if (seasons.isEmpty()) throw new IllegalArgumentException("No seasons are known");
        if (StringUtils.isBlank(yyyymmdd)) {
            int year = SeasonManager.seasonByDate(today);
            if (seasons.containsKey(year)) {
                Season season = seasons.get(year);
                return season
                        .gameDates()
                        .stream()
                        .filter(d -> !d.isAfter(today))
                        .max(Comparator.naturalOrder())
                        .orElse(today);
            } else {
                return today;
            }
        } else {
            return LocalDate.parse(yyyymmdd, DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
    }


    public GamesPage getGamesPage(String yyyymmdd) {
        Map<Integer, Season> seasons = seasonRepo.findAllByOrderBySeasonDesc().stream().collect(Collectors.toMap(Season::getSeason, Function.identity()));
        LocalDate today = LocalDate.now();
        LocalDate date = getDate(yyyymmdd, today, seasons);
        LocalDate asOf = (date.isAfter(today)) ? today : date;
        int season = SeasonManager.seasonByDate(date);
        if (seasons.containsKey(season)) {
            return new GamesPage(date, getPrev(date, seasons), getNext(date, seasons), season, asOf, createGameLines(date, seasons.get(season)));
        } else {
            return new GamesPage(date, date.minusDays(1), date.plusDays(1), season, asOf, Collections.emptyList());
        }
    }

    private List<GameLine> createGameLines(LocalDate asOf, Season season) {
        Map<String, TeamLine> teamData = new HashMap<>();
        season.gameDates().stream().filter(d -> !d.isAfter(asOf)).forEach(d -> {
            season.getGamesForDate(d).forEach(g -> {
                Team homeTeam = g.getHomeTeam();
                Team awayTeam = g.getAwayTeam();
                Integer homeScore = g.getHomeScore();
                Integer awayScore = g.getAwayScore();
                if (homeScore != null && awayScore != null) {
                    update(teamData, homeTeam, homeScore, awayScore);
                    update(teamData, awayTeam, awayScore, homeScore);
                }
            });
        });
        return season.getGamesForDate(asOf).stream().map(g -> new GameLine(g, teamData.get(g.getHomeTeam().getKey()), teamData.get(g.getAwayTeam().getKey()))).toList();

    }

    private static void update(Map<String, TeamLine> teamData, Team team, int score, int oppScore) {
        String key = team.getKey();
        teamData.compute(key, (k, v) -> v == null ? TeamLine.create(team.getName(), key).update(score, oppScore) : v.update(score, oppScore));
    }


    public LocalDate getPrev(LocalDate d, Map<Integer, Season> seasons) {
        LocalDate previousDate = d.minusDays(1);
        int year = SeasonManager.seasonByDate(d);

        if (!seasons.containsKey(year)) {
            return previousDate;
        }

        Season season = seasons.get(year);
        if (previousDate.isBefore(season.firstGameDate()) && seasons.containsKey(year - 1)) {
            return seasons.get(year - 1).lastGameDate();
        } else {
            return previousDate;
        }
    }

    public LocalDate getNext(LocalDate d, Map<Integer, Season> seasons) {
        LocalDate nextDate = d.plusDays(1);
        int year = SeasonManager.seasonByDate(d);

        if (!seasons.containsKey(year)) {
            return nextDate;
        }

        Season season = seasons.get(year);
        if (nextDate.isAfter(season.lastGameDate()) && seasons.containsKey(year + 1)) {
            return seasons.get(year + 1).firstGameDate();
        } else {
            return nextDate;
        }
    }
}
