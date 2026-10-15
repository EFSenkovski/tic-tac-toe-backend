package com.wsopt.tictactoebackend.core.domain.game.strategy;

import com.wsopt.tictactoebackend.core.domain.game.Game;

public interface MoveStrategy {
    int getNextMoveIndex(Game game);
}
