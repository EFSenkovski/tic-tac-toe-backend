package com.wsopt.tictactoebackend.core.app.port.out;

import com.wsopt.tictactoebackend.core.domain.game.Figure;
import com.wsopt.tictactoebackend.core.domain.game.player.Player;

import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository {
    Optional<Player> findByExternalId(UUID playerId);
}
