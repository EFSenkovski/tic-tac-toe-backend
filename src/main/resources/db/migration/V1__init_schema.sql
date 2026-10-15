-- Initial schema for the tic-tac-toe backend.
-- Mirrors the JPA entities in core.infrastructure.persistence.game.
--
-- Each entity declares its own @SequenceGenerator with allocationSize = 1, and
-- Hibernate validates the sequence increment against it on startup, so every
-- sequence below must use INCREMENT BY 1.

-- users ---------------------------------------------------------------------
CREATE SEQUENCE users_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE users (
    id          BIGINT       NOT NULL,
    external_id UUID         NOT NULL,
    email       VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_external_id UNIQUE (external_id)
);

-- players -------------------------------------------------------------------
CREATE SEQUENCE players_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE players (
    id          BIGINT       NOT NULL,
    external_id UUID         NOT NULL,
    figure      VARCHAR(255) NOT NULL,
    type        VARCHAR(255) NOT NULL,
    user_id     BIGINT,
    CONSTRAINT pk_players PRIMARY KEY (id),
    CONSTRAINT uk_players_external_id UNIQUE (external_id),
    CONSTRAINT fk_players_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_players_user_id ON players (user_id);

-- boards --------------------------------------------------------------------
CREATE SEQUENCE boards_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE boards (
    id          BIGINT NOT NULL,
    external_id UUID,
    state       VARCHAR(255),
    CONSTRAINT pk_boards PRIMARY KEY (id),
    CONSTRAINT uk_boards_external_id UNIQUE (external_id)
);

-- games ---------------------------------------------------------------------
CREATE SEQUENCE games_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE games (
    id                BIGINT       NOT NULL,
    external_id       UUID         NOT NULL,
    player_one_id     BIGINT,
    player_two_id     BIGINT,
    board_id          BIGINT,
    status            VARCHAR(255) NOT NULL,
    phase             VARCHAR(255) NOT NULL,
    current_player_id BIGINT       NOT NULL,
    CONSTRAINT pk_games PRIMARY KEY (id),
    CONSTRAINT uk_games_external_id UNIQUE (external_id),
    -- @OneToOne: each player and each board belong to at most one game
    CONSTRAINT uk_games_player_one_id UNIQUE (player_one_id),
    CONSTRAINT uk_games_player_two_id UNIQUE (player_two_id),
    CONSTRAINT uk_games_board_id UNIQUE (board_id),
    CONSTRAINT fk_games_player_one FOREIGN KEY (player_one_id) REFERENCES players (id),
    CONSTRAINT fk_games_player_two FOREIGN KEY (player_two_id) REFERENCES players (id),
    CONSTRAINT fk_games_board FOREIGN KEY (board_id) REFERENCES boards (id),
    CONSTRAINT fk_games_current_player FOREIGN KEY (current_player_id) REFERENCES players (id)
);
