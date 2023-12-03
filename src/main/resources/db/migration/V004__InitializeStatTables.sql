CREATE TABLE model_statistic
(
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(64) NOT NULL UNIQUE,
    higher_is_better BOOLEAN     NOT NULL
);

CREATE UNIQUE INDEX ON model_statistic (name);

CREATE TABLE statistic_values
(
    id                 BIGSERIAL PRIMARY KEY,
    model_statistic_id BIGINT    NOT NULL REFERENCES model_statistic (id),
    season_id          BIGINT    NOT NULL REFERENCES season (id),
    date               DATE      NOT NULL,  
    team_id            BIGINT    NOT NULL REFERENCES team (id),
    value              FLOAT    NOT NULL,
    published_at       TIMESTAMP NOT NULL
);