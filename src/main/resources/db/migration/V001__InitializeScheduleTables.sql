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
    espn_id       VARCHAR(24)  NULL,
    scrape_src_id BIGINT       NOT NULL,
    published_at  TIMESTAMP    NOT NULL
);

CREATE UNIQUE INDEX ON team (key);

CREATE TABLE conference
(
    id            BIGSERIAL PRIMARY KEY,
    key           VARCHAR(32)  NOT NULL UNIQUE,
    name          VARCHAR(64)  NOT NULL UNIQUE,
    alt_name      VARCHAR(32)  NULL,
    logo_url      varchar(256) NULL,
    espn_id       VARCHAR(24)  NULL,
    scrape_src_id BIGINT       NOT NULL,
    published_at  TIMESTAMP    NOT NULL
);

CREATE UNIQUE INDEX ON conference (key);


CREATE TABLE conference_maps
(
    id BIGSERIAL PRIMARY KEY,
    season_id BIGINT NOT NULL,
    conference_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    scrape_src_id BIGINT       NOT NULL,
    published_at  TIMESTAMP    NOT NULL
);

CREATE UNIQUE INDEX ON conference_maps(season_id, team_id);

CREATE TABLE games
(
    id BIGSERIAL PRIMARY KEY
);
