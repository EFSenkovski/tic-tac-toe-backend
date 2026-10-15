package com.wsopt.tictactoebackend.core.app.port.in;

import com.wsopt.tictactoebackend.core.domain.game.Figure;
import com.wsopt.tictactoebackend.core.domain.game.player.Player;

public interface CreatePlayerUseCase {
    Player createPlayer(String input, Figure figure);
}
