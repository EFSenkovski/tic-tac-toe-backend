package com.wsopt.tictactoebackend.core.domain.game.player;

import com.wsopt.tictactoebackend.core.domain.game.Figure;
import com.wsopt.tictactoebackend.core.domain.game.strategy.EasyMoveStrategy;
import com.wsopt.tictactoebackend.core.domain.game.strategy.HardMoveStrategy;
import com.wsopt.tictactoebackend.core.domain.game.strategy.MediumMoveStrategy;
import com.wsopt.tictactoebackend.core.domain.game.strategy.MoveStrategy;

import java.util.UUID;

public class PlayerFactory {
    public static Player buildPlayer(String userInput, Figure figure) {
        return buildPlayer(PlayerType.from(userInput), figure);
    }

    public static Player buildPlayer(PlayerType playerType, Figure figure) {
        return PlayerType.isHuman(playerType) ?
                new Player(figure, PlayerType.USER)
                : new AiPlayer(figure, strategyFor(playerType), playerType);
    }

    public static Player rebuildPlayer(Figure figure, UUID externalId, PlayerType playerType, UUID userExternalId, Rematch rematch) {
        return PlayerType.isHuman(playerType) ?
                new Player(figure, externalId, playerType, userExternalId, rematch)
                : new AiPlayer(figure, externalId, strategyFor(playerType), playerType);
    }

    private static MoveStrategy strategyFor(PlayerType type) {
        return switch (type) {
            case USER -> throw new IllegalArgumentException("Player of type user can't have an AI strategy");
            case EASY -> new EasyMoveStrategy();
            case MEDIUM -> new MediumMoveStrategy();
            case HARD -> new HardMoveStrategy();
        };
    }
}
