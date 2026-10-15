-- Rematch: a Player of a finished Game asks for another Game with the same
-- Player Types and every Figure swapped. Mirrors PlayerJpaEntity.rematch and
-- GameJpaEntity.nextGameExternalId.

-- players -------------------------------------------------------------------
-- NOT_ASKED, ASKED or SEATED (handed their seat in the next game).
ALTER TABLE players
    ADD COLUMN rematch VARCHAR(255) NOT NULL DEFAULT 'NOT_ASKED';

-- games ---------------------------------------------------------------------
-- The rematch of this game, once every human player has asked for it.
ALTER TABLE games
    ADD COLUMN next_game_external_id UUID;

ALTER TABLE games
    ADD CONSTRAINT uk_games_next_game_external_id UNIQUE (next_game_external_id),
    ADD CONSTRAINT fk_games_next_game FOREIGN KEY (next_game_external_id) REFERENCES games (external_id);
