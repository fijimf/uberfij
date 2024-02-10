package com.fijimf.deepfij.services.schedule;

import com.fijimf.deepfij.db.model.schedule.Game;
import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.schedule.Team;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatistic;
import com.fijimf.deepfij.db.repo.schedule.SeasonRepo;
import com.fijimf.deepfij.db.repo.schedule.TeamRepo;
import com.fijimf.deepfij.db.repo.statistic.DailyTeamStatisticRepo;
import com.fijimf.deepfij.model.GameScatterData;
import com.fijimf.deepfij.model.TeamPage;
import com.fijimf.deepfij.services.rest.StatsObservation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TeamManager {
    private final TeamRepo teamRepo;

    private final SeasonRepo seasonRepo;

    private final DailyTeamStatisticRepo statisticRepo;

    public TeamManager(TeamRepo teamRepo, SeasonRepo seasonRepo, DailyTeamStatisticRepo statisticRepo) {
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
}
