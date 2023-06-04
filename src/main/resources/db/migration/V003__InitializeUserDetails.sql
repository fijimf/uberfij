CREATE TABLE "user"
(
    id                    BIGSERIAL PRIMARY KEY,
    username              VARCHAR(64) NOT NULL,
    password              VARCHAR(96) NOT NULL,
    email                 VARCHAR(96) NOT NULL,
    locked                BOOLEAN     NOT NULL,
    activated             BOOLEAN     NOT NULL,
    expire_credentials_at TIMESTAMP   NULL
);

CREATE UNIQUE INDEX ON "user" (username);
CREATE UNIQUE INDEX ON "user" (email);

CREATE TABLE auth_token
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT      NOT NULL,
    token      VARCHAR(48) NOT NULL,
    expires_at TIMESTAMP   NOT NULL
);

CREATE UNIQUE INDEX ON auth_token (token);

CREATE TABLE role
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL
);

CREATE UNIQUE INDEX ON role (name);

CREATE TABLE user_role
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user" (id),
    role_id BIGINT NOT NULL REFERENCES role (id)
);

CREATE UNIQUE INDEX ON user_role (user_id, role_id);

insert into role(name)
values ('ADMIN');
insert into role(name)
values ('USER');


