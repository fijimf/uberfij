package com.fijimf.deepfij.services.schedule;

import com.fijimf.deepfij.db.model.schedule.Game;
import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.schedule.Team;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatistic;
import com.fijimf.deepfij.db.repo.schedule.SeasonRepo;
import com.fijimf.deepfij.db.repo.schedule.TeamRepo;
import com.fijimf.deepfij.db.repo.statistic.DailyTeamStatisticRepo;
import com.fijimf.deepfij.model.GameScatterData;
import com.fijimf.deepfij.model.GamesPage;
import com.fijimf.deepfij.model.TeamPage;
import com.fijimf.deepfij.scraping.SeasonManager;
import com.fijimf.deepfij.services.rest.StatsObservation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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

    public LocalDate getDate(String yyyymmdd) {
        if (StringUtils.isBlank(yyyymmdd)) {
            LocalDate today = LocalDate.now();
            Season season = seasonRepo.findFirstByOrderBySeasonDesc().orElseThrow();
            return season
                    .gameDates()
                    .stream()
                    .filter(d -> !d.isAfter(today))
                    .max(Comparator.naturalOrder())
                    .orElse(today);
        } else {
            return LocalDate.parse(yyyymmdd, DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
    }

    public GamesPage getGamesPage(String yyyymmdd) {
        LocalDate date = getDate(yyyymmdd);
        Optional<Season> optSeason = seasonRepo.findBySeason(SeasonManager.seasonByDate(date));
        if (optSeason.isPresent()) {
            return new GamesPage(date, optSeason.get().getGamesFroDate(date));
        } else {
            Season season = seasonRepo.findFirstByOrderBySeasonDesc().orElseThrow();
            LocalDate d2 = season.gameDates().get(season.gameDates().size() - 1);
            return new GamesPage(d2, season.getGamesFroDate(date));
        }
    }
}

