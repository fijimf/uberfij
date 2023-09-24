CREATE TABLE season
(
    id     BIGSERIAL PRIMARY KEY,
    season INT NOT NULL
);

CREATE UNIQUE INDEX ON season (season);

CREATE TABLE team
(
    id            BIGSERIAL PRIMARY KEY,
    key           VARCHAR(48)  NOT NULL UNIQUE,
    name          VARCHAR(64)  NOT NULL UNIQUE,
    nickname      VARCHAR(64)  NOT NULL,
    long_name     VARCHAR(96)  NOT NULL UNIQUE,
    alt_name_1    VARCHAR(64)  NULL,
    alt_name_2    VARCHAR(64)  NULL,
    color         VARCHAR(12)  NULL,
    alt_color     VARCHAR(12)  NULL,
    logo_url      VARCHAR(256) NULL,
    espn_id       VARCHAR(36)  NULL,
    scrape_src_id BIGINT       NOT NULL,
    published_at  TIMESTAMP    NOT NULL
);

CREATE UNIQUE INDEX ON team (key);
CREATE UNIQUE INDEX ON team (espn_id);

CREATE TABLE conference
(
    id            BIGSERIAL PRIMARY KEY,
    key           VARCHAR(32)  NOT NULL UNIQUE,
    name          VARCHAR(64)  NOT NULL UNIQUE,
    alt_name      VARCHAR(32)  NULL,
    logo_url      varchar(256) NULL,
    espn_id       VARCHAR(36)  NULL,
    scrape_src_id BIGINT       NOT NULL,
    published_at  TIMESTAMP    NOT NULL
);

CREATE UNIQUE INDEX ON conference (key);
CREATE UNIQUE INDEX ON conference (espn_id);


CREATE TABLE conference_maps
(
    id            BIGSERIAL PRIMARY KEY,
    season_id     BIGINT    NOT NULL REFERENCES season (id),
    conference_id BIGINT    NOT NULL REFERENCES conference (id),
    team_id       BIGINT    NOT NULL REFERENCES team (id),
    scrape_src_id BIGINT    NOT NULL,
    published_at  TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX ON conference_maps (season_id, team_id);

CREATE TABLE games
(
    id                 BIGSERIAL PRIMARY KEY,
    date               DATE        NOT NULL,
    scoreboard_key     DATE        NOT NULL,
    season_id          BIGINT      NOT NULL REFERENCES season (id),
    home_team_id       BIGINT      NOT NULL REFERENCES team (id),
    away_team_id       BIGINT      NOT NULL REFERENCES team (id),
    home_score         INT         NULL,
    away_score         INT         NULL,
    num_periods        INT         NULL,
    is_neutral_site    BOOLEAN     NULL,
    location           VARCHAR(48) NULL,
    spread             FLOAT       NULL,
    over_under         FLOAT       NULL,
    is_conf_tournament BOOLEAN     NULL,
    is_ncaa_tournament BOOLEAN     NULL,
    espn_id            VARCHAR(36) NOT NULL,
    scrape_src_id      BIGINT      NOT NULL,
    published_at       TIMESTAMP   NOT NULL
);

CREATE UNIQUE INDEX ON games (espn_id);
