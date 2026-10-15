package com.wsopt.tictactoebackend.core.infrastructure.persistence.game;

import com.wsopt.tictactoebackend.core.domain.game.Game;
import com.wsopt.tictactoebackend.core.infrastructure.persistence.board.BoardMapper;
import com.wsopt.tictactoebackend.core.infrastructure.persistence.player.PlayerMapper;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {

    private final PlayerMapper playerMapper;
    private final BoardMapper boardMapper;

    public GameMapper(PlayerMapper playerMapper, BoardMapper boardMapper) {
        this.playerMapper = playerMapper;
        this.boardMapper = boardMapper;
    }

    public GameJpaEntity toGameJpaEntity(Game game) {
        var playerOne = playerMapper.toPlayerJpaEntity(game.getPlayerOne());
        var playerTwo = playerMapper.toPlayerJpaEntity(game.getPlayerTwo());
        var currentPlayer = game.getCurrentPlayer() == game.getPlayerOne() ? playerOne : playerTwo;

        return GameJpaEntity.newGameJpaEntity()
                .externalId(game.getExternalId())
                .playerOne(playerOne)
                .playerTwo(playerTwo)
                .currentPlayer(currentPlayer)
                .board(boardMapper.toBoardJpaEntity(game.getBoard()))
                .status(game.getGameStatus())
                .phase(game.getGamePhase())
                .nextGameExternalId(game.getNextGameId())
                .build();
    }

    public void copyStateTo(Game game, GameJpaEntity jpaEntity) {
        jpaEntity.getBoard().setState(game.getBoard().calculateState());
        jpaEntity.setStatus(game.getGameStatus());
        jpaEntity.setPhase(game.getGamePhase());
        jpaEntity.setNextGameExternalId(game.getNextGameId());
    }

    public Game toGame(GameJpaEntity gameJpaEntity) {
        var playerOne = playerMapper.toPlayer(gameJpaEntity.getPlayerOne());
        var playerTwo = playerMapper.toPlayer(gameJpaEntity.getPlayerTwo());
        var currentPlayer = gameJpaEntity.getCurrentPlayer() == gameJpaEntity.getPlayerOne() ? playerOne : playerTwo;

        return new Game(
                gameJpaEntity.getExternalId(),
                boardMapper.toBoard(gameJpaEntity.getBoard()),
                gameJpaEntity.getStatus(),
                gameJpaEntity.getPhase(),
                playerOne,
                playerTwo,
                currentPlayer,
                gameJpaEntity.getNextGameExternalId()
        );
    }
}
