CREATE TABLE daily_team_statistic_summary
(
    id           BIGSERIAL PRIMARY KEY,
    season_id    BIGSERIAL REFERENCES season (id),
    date         DATE             NOT NULL,
    statistic    VARCHAR(255)     NOT NULL,
    n            INTEGER          NOT NULL,
    mean         DOUBLE PRECISION NOT NULL,
    min          DOUBLE PRECISION NOT NULL,
    max          DOUBLE PRECISION NOT NULL,
    median       DOUBLE PRECISION NOT NULL,
    percentile25 DOUBLE PRECISION NOT NULL,
    percentile75 DOUBLE PRECISION NOT NULL,
    std_dev      DOUBLE PRECISION NULL,
    kurtosis     DOUBLE PRECISION NULL,
    skewness     DOUBLE PRECISION NULL
);
CREATE UNIQUE INDEX daily_team_statistic_summary_season_id_date_statistic_idx
    ON daily_team_statistic_summary (season_id, date, statistic);
CREATE INDEX daily_team_statistic_summary_season_id_idx
    ON daily_team_statistic_summary (season_id, statistic, date);

CREATE TABLE daily_team_statistic
(
    id         BIGSERIAL PRIMARY KEY,
    summary_id BIGSERIAL REFERENCES daily_team_statistic_summary (id),
    team_id    BIGSERIAL REFERENCES team (id),
    value      DOUBLE PRECISION NOT NULL
);

CREATE UNIQUE INDEX daily_team_statistic_summary_id_team_id_statistic_idx
    ON daily_team_statistic (summary_id, team_id);
