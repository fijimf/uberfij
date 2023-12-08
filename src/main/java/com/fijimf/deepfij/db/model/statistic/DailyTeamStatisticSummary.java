package com.fijimf.deepfij.db.model.statistic;

import com.fijimf.deepfij.db.model.schedule.Season;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "daily_team_statistic_summary")
public class DailyTeamStatisticSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "season_id")
    private Season season;
    private LocalDate date;
    private String statistic;
    private Integer n;
    private Double mean;
    private Double min;
    private Double max;
    private Double median;
    private Double percentile25;
    private Double percentile75;
    @Column(name = "std_dev")
    private Double stdDev;
    private Double kurtosis;
    private Double skewness;

    public DailyTeamStatisticSummary() {
    }

    public DailyTeamStatisticSummary(Long id, Season season, LocalDate date, String statistic, Double mean, Integer n, Double min, Double max, Double median, Double percentile25, Double percentile75, Double stdDev, Double kurtosis, Double skewness) {
        this.id = id;
        this.season = season;
        this.date = date;
        this.statistic = statistic;
        this.n = n;
        this.mean = mean;
        this.min = min;
        this.max = max;
        this.median = median;
        this.percentile25 = percentile25;
        this.percentile75 = percentile75;
        this.stdDev = stdDev;
        this.kurtosis = kurtosis;
        this.skewness = skewness;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStatistic(String statistic) {
        this.statistic = statistic;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public void setMean(Double mean) {
        this.mean = mean;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public void setMedian(Double median) {
        this.median = median;
    }

    public void setPercentile25(Double percentile25) {
        this.percentile25 = percentile25;
    }

    public void setPercentile75(Double percentile75) {
        this.percentile75 = percentile75;
    }

    public void setStdDev(Double stdDev) {
        this.stdDev = stdDev;
    }

    public void setKurtosis(Double kurtosis) {
        this.kurtosis = kurtosis;
    }

    public void setSkewness(Double skewness) {
        this.skewness = skewness;
    }

    public Season getSeason() {
        return season;
    }

    public LocalDate getDate() {
        return date;
    }

    public Integer getN() {
        return n;
    }

    public Double getMean() {
        return mean;
    }

    public Double getMin() {
        return min;
    }

    public Double getMax() {
        return max;
    }

    public Double getMedian() {
        return median;
    }

    public Double getPercentile25() {
        return percentile25;
    }

    public Double getPercentile75() {
        return percentile75;
    }

    public Double getStdDev() {
        return stdDev;
    }

    public Double getKurtosis() {
        return kurtosis;
    }

    public Double getSkewness() {
        return skewness;
    }

    public String getStatistic() {
        return statistic;
    }
}
