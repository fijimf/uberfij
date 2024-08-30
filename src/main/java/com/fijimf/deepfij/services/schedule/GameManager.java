package com.fijimf.deepfij.services.schedule;

import com.fijimf.deepfij.db.model.schedule.Conference;
import com.fijimf.deepfij.db.model.schedule.Game;
import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.schedule.Team;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatistic;
import com.fijimf.deepfij.db.repo.schedule.ConferenceRepo;
import com.fijimf.deepfij.db.repo.schedule.GameRepo;
import com.fijimf.deepfij.db.repo.schedule.SeasonRepo;
import com.fijimf.deepfij.db.repo.schedule.TeamRepo;
import com.fijimf.deepfij.db.repo.statistic.DailyTeamStatisticRepo;
import com.fijimf.deepfij.model.Record;
import com.fijimf.deepfij.model.*;
import com.fijimf.deepfij.scraping.SeasonManager;
import com.fijimf.deepfij.services.rest.StatsObservation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealMatrixFormat;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
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
    private final ConferenceRepo conferenceRepo;

    private final GameRepo gameRepo;

    private final SeasonRepo seasonRepo;

    private final DailyTeamStatisticRepo statisticRepo;

    public GameManager(TeamRepo teamRepo, ConferenceRepo conferenceRepo, GameRepo gameRepo, SeasonRepo seasonRepo, DailyTeamStatisticRepo statisticRepo) {
        this.teamRepo = teamRepo;
        this.conferenceRepo = conferenceRepo;
        this.gameRepo = gameRepo;
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

    public Optional<GameSnapshot> getGameSnapshot(Long id) {
        return gameRepo.findById(id).map(g -> new GameSnapshot(id, g.getSlug(), g.getDate(), createTeamSnapshot(g.getHomeTeam(), g.getDate()), g.getHomeScore(), createTeamSnapshot(g.getAwayTeam(), g.getDate()), g.getAwayScore(), g.getSpread(), g.getOverUnder()));
    }

    private TeamSnapshot createTeamSnapshot(Team team, LocalDate asOf) {
        int year = SeasonManager.seasonByDate(asOf);
        String conference = conferenceRepo.findConferenceForTeamYear(team.getId(), year).map(Conference::getName).orElse("");
        List<Game> games = gameRepo.findAllByTeamSeason(team.getId(), year)
                .stream()
                .filter(g -> g.getDate().isBefore(asOf))
                .filter(g -> g.getHomeScore() != null && g.getAwayScore() != null)
                .toList();
        List<TeamGameSnapshot> teamGames = games.stream().map(g -> makeTeamGameSnapshot(team, g)).toList();
        Record overall = Record.createRecord("Overall", team, games);
        Record last5 = Record.createRecord("Last 5", team, games.subList(0, Math.min(5, games.size())));
        double[] pf = games.stream().mapToDouble(g -> g.getScore(team).doubleValue()).toArray();
        double[] pa = games.stream().mapToDouble(g -> g.getOppScore(team).doubleValue()).toArray();
        MultivariateSummaryStatistics scoringStats = calculateScoringStats(team, games);
        RealMatrix cov = scoringStats.getCovariance();

        System.err.println(RealMatrixFormat.getInstance().format(cov));

        double a = cov.getEntry(0, 0);
        double b = cov.getEntry(0, 1);
        double c = cov.getEntry(1, 0);
        double d = cov.getEntry(1, 1);
        double corr = b / (Math.sqrt(a) * Math.sqrt(d));

        double trace = cov.getTrace();
        double m = trace / 2;
        double det = determinant2x2(cov);
        double q = Math.sqrt(m * m - det);
        double[] eigenvalues = {m + q, m - q};

        double c95angle = Math.atan2(eigenvalues[1] - d, b);
        double c95majorAxis =  Math.sqrt(eigenvalues[0] * 5.99);
        double c95minorAxis =  Math.sqrt(eigenvalues[1] * 5.99);
        Percentile q1 = new Percentile(25);
        Percentile q3 = new Percentile(75);

        double pointsForAvg = scoringStats.getMean()[0];
        double pointsForStdDev = scoringStats.getStandardDeviation()[0];
        double pointsAgainstAvg = scoringStats.getMean()[1];
        double pointsAgainstStdDev = scoringStats.getStandardDeviation()[1];
        double c95pfAxis = pointsForStdDev > pointsAgainstStdDev? c95majorAxis:c95minorAxis;
        double c95paAxis = pointsForStdDev <= pointsAgainstStdDev? c95majorAxis:c95minorAxis;
        System.err.println("----");
        System.err.println("PF "+pointsForStdDev+" "+c95pfAxis);
        System.err.println("PA "+pointsAgainstStdDev+" "+c95paAxis);
        System.err.println("----");
        return new TeamSnapshot(
                new SimpleTeam(team, conference),
                asOf,
                overall,
                last5,
                teamGames,
                pointsForAvg,//pointsFor.getMean(),
                pointsForStdDev,//.getStandardDeviation(),
                q1.evaluate(pf),
                q3.evaluate(pf),
                pointsAgainstAvg,//pointsFor.getMean(),
                pointsAgainstStdDev,//
                q1.evaluate(pa),
                q3.evaluate(pa),
                corr,
                c95pfAxis,
                c95paAxis,
                c95angle

        );
    }

    private double determinant2x2(RealMatrix cov) {
        if (cov.getColumnDimension() != 2 || cov.getRowDimension() != 2) throw new IllegalArgumentException("Matrix must be 2x2");
        return cov.getEntry(0, 0) * cov.getEntry(1, 1) - cov.getEntry(0, 1) * cov.getEntry(1, 0);
    }

    private TeamGameSnapshot makeTeamGameSnapshot(Team team, Game game) {
        Team opponent = game.getOpponent(team);
        Conference conference = game.getSeason().getConference(opponent);
        SimpleTeam oppTeam = new SimpleTeam(opponent.getId(), opponent.getKey(), opponent.getName(), conference.getName(), opponent.getNickname(), opponent.getColor(), opponent.getLogoUrl());
        return new TeamGameSnapshot(game.getId(), oppTeam, game.getScore(team), game.getOppScore(team), game.getDate());
    }

    private static MultivariateSummaryStatistics calculateScoringStats(Team team, List<Game> games) {
        MultivariateSummaryStatistics mvs = new MultivariateSummaryStatistics(2, true);
        games.forEach(g -> {
            mvs.addValue(new double[]{g.getScore(team), g.getOppScore(team)});
        });
        return mvs;
    }
}
