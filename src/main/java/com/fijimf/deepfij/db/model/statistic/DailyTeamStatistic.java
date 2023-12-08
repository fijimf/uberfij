package com.fijimf.deepfij.db.model.statistic;

import com.fijimf.deepfij.db.model.schedule.Team;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "daily_team_statistic")
public class DailyTeamStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "summary_id")
    private DailyTeamStatisticSummary summary;
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    private Double value;


    public DailyTeamStatistic() {
    }

    public DailyTeamStatistic(Long id, DailyTeamStatisticSummary summary, Team team,  Double value) {
        this.summary = summary;
        this.team = team;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DailyTeamStatisticSummary getSummary() {
        return summary;
    }

    public void setSummary(DailyTeamStatisticSummary summary) {
        this.summary = summary;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
