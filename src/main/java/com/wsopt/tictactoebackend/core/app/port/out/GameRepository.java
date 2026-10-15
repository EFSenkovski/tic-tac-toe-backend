package com.wsopt.tictactoebackend.core.app.port.out;

import com.wsopt.tictactoebackend.core.domain.game.Game;

import java.util.Optional;
import java.util.UUID;

public interface GameRepository {
    Game saveGame(Game game);
    Optional<Game> findByExternalId(UUID externalId);

    /**
     * Like {@link #findByExternalId(UUID)}, but holds a write lock on the Game until the transaction ends.
     */
    Optional<Game> findByExternalIdForUpdate(UUID externalId);
}
