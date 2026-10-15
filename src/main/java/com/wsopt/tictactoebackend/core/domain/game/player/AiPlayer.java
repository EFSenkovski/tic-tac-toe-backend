package com.wsopt.tictactoebackend.core.domain.game.player;

import com.wsopt.tictactoebackend.core.domain.game.Figure;
import com.wsopt.tictactoebackend.core.domain.game.Game;
import com.wsopt.tictactoebackend.core.domain.game.strategy.MoveStrategy;

import java.util.UUID;

public class AiPlayer extends Player{
    private final MoveStrategy strategy;

    public AiPlayer(Figure figure, MoveStrategy strategy, PlayerType type) {
        super(figure, type);
        this.strategy = strategy;
    }

    public AiPlayer(Figure figure, UUID externalId, MoveStrategy strategy, PlayerType type) {
        super(figure, externalId, type, null, Rematch.NOT_ASKED);
        this.strategy = strategy;
    }

    public int getNextMoveIndex(Game game) {
        return strategy.getNextMoveIndex(game);
    }
}
