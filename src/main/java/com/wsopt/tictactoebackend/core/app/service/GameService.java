package com.wsopt.tictactoebackend.core.app.service;

import com.wsopt.tictactoebackend.core.app.exception.ApplicationException;
import com.wsopt.tictactoebackend.core.app.exception.GameNotFoundException;
import com.wsopt.tictactoebackend.core.app.exception.PlayerNotFoundException;
import com.wsopt.tictactoebackend.core.app.port.in.CreatePlayerUseCase;
import com.wsopt.tictactoebackend.core.app.port.in.game.CreateGameUseCase;
import com.wsopt.tictactoebackend.core.app.port.in.game.GetGameUseCase;
import com.wsopt.tictactoebackend.core.app.port.in.game.MakeMoveUseCase;
import com.wsopt.tictactoebackend.core.app.port.in.game.RematchUseCase;
import com.wsopt.tictactoebackend.core.app.port.in.game.TakePlayerSeatUseCase;
import com.wsopt.tictactoebackend.core.app.port.in.game.request.GameRequest;
import com.wsopt.tictactoebackend.core.app.port.in.game.request.MakeMoveRequest;
import com.wsopt.tictactoebackend.core.app.port.in.game.request.RematchRequest;
import com.wsopt.tictactoebackend.core.app.port.in.game.response.GameResponse;
import com.wsopt.tictactoebackend.core.app.port.in.shared.PlayerResponse;
import com.wsopt.tictactoebackend.core.app.port.out.GameRepository;
import com.wsopt.tictactoebackend.core.app.port.out.PlayerRepository;
import com.wsopt.tictactoebackend.core.domain.game.Figure;
import com.wsopt.tictactoebackend.core.domain.game.Game;
import com.wsopt.tictactoebackend.core.domain.game.GamePhase;
import com.wsopt.tictactoebackend.core.domain.game.player.Player;
import com.wsopt.tictactoebackend.core.domain.game.player.PlayerType;
import com.wsopt.tictactoebackend.core.domain.game.player.Rematch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GameService implements CreateGameUseCase, TakePlayerSeatUseCase, MakeMoveUseCase, GetGameUseCase, RematchUseCase {
    private final CreatePlayerUseCase playerCreator;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    public GameService(CreatePlayerUseCase playerCreator, GameRepository gameRepository, PlayerRepository playerRepository) {
        this.playerCreator = playerCreator;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    @Transactional
    public GameResponse createGame(GameRequest request) {

        var playerOneType = PlayerType.from(request.playerOne());
        var playerTwoType = PlayerType.from(request.playerTwo());

        if (bothPlayersAreHuman(playerOneType, playerTwoType)) {
            if (request.myFigure() == null) {
                throw new ApplicationException("In a game where both players are human, a 'myFigure' value with 'X' or 'O' is required");
            }
        }

        Player playerOne;
        Player playerTwo;
        if (bothPlayersAreHuman(playerOneType, playerTwoType)) {
            var iAmFirst = (request.myFigure().equals(Figure.X));
            if (iAmFirst) {
                playerOne = playerCreator.createPlayer(request.playerOne(), Figure.X);
                playerTwo = null;
            } else {
                playerOne = null;
                playerTwo = playerCreator.createPlayer(request.playerTwo(), Figure.O);
            }
        } else {
            playerOne = playerCreator.createPlayer(request.playerOne(), Figure.X);
            playerTwo = playerCreator.createPlayer(request.playerTwo(), Figure.O);
        }

        Game game = new Game(playerOne, playerTwo);
        var savedGame = this.gameRepository.saveGame(game);

        PlayerResponse myPlayer = null;
        if (oneOfThePlayersIsHuman(playerOneType, playerTwoType)) {
            var player = requesterIsPlayerOne(request, playerOneType, playerTwoType) ?
                    savedGame.getPlayerOne() :
                    savedGame.getPlayerTwo();
            myPlayer = new PlayerResponse(player.getExternalId(), player.getFigure().getStringFromFigure());
        }

        return toResponse(savedGame, myPlayer);
    }

    @Override
    public GameResponse takePlayerSeat(UUID gameId) {
        var game = this.gameRepository.findByExternalId(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        if (game.areTwoPlayerSeatsTaken()) {
            throw new ApplicationException(String.format("Game %s already has two players!", gameId));
        }

        var current = game.getCurrentPlayer();

        var player = playerCreator.createPlayer("user", current.getFigure().opposite());
        game.setMissingPlayer(player);

        var savedGame = this.gameRepository.saveGame(game);

        var playerResponse = new PlayerResponse(player.getExternalId(), player.getFigure().getStringFromFigure());
        return new GameResponse(savedGame.getExternalId(),
                current.getFigure(),
                playerResponse,
                savedGame.getGamePhase(),
                savedGame.getGameStatus(),
                savedGame.getBoard().calculateState(),
                savedGame.getRematchAskedBy(),
                savedGame.getNextGameId());
    }

    @Override
    @Transactional
    public GameResponse makeMove(MakeMoveRequest request) {
        var game = this.gameRepository.findByExternalId(request.gameId())
                .orElseThrow(() -> new GameNotFoundException(request.gameId()));

        if (game.getGamePhase() != GamePhase.PLAYING) {
            throw new ApplicationException(String.format("Game %s is not in progress (phase %s)", request.gameId(), game.getGamePhase()));
        }

        var player = this.playerRepository.findByExternalId(request.playerId())
                .orElseThrow(() -> new PlayerNotFoundException(request.playerId()));

        game.makeMove(request.index(), player);
        var savedGame = this.gameRepository.saveGame(game);

        return toResponse(savedGame, toPlayerResponse(player));
    }

    @Override
    @Transactional(readOnly = true)
    public GameResponse getGame(UUID gameId) {
        var game = this.gameRepository.findByExternalId(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        return toResponse(game, null);
    }

    @Override
    @Transactional
    public GameResponse rematch(RematchRequest request) {
        var game = this.gameRepository.findByExternalIdForUpdate(request.gameId())
                .orElseThrow(() -> new GameNotFoundException(request.gameId()));

        if (game.getGamePhase() != GamePhase.FINISHED) {
            throw new ApplicationException(String.format("Game %s is not over (phase %s)", request.gameId(), game.getGamePhase()));
        }

        var player = this.playerRepository.findByExternalId(request.playerId())
                .orElseThrow(() -> new PlayerNotFoundException(request.playerId()));

        if (player.getRematch() == Rematch.SEATED) {
            throw new ApplicationException(String.format("Player %s has already taken their seat in a rematch", request.playerId()));
        }

        game.askForRematch(player);

        if (game.getNextGameId() == null && !game.isRematchAgreed()) {
            return toResponse(this.gameRepository.saveGame(game), null);
        }

        var nextGame = game.getNextGameId() == null ?
                this.gameRepository.saveGame(game.rematch()) :
                this.gameRepository.findByExternalId(game.getNextGameId())
                        .orElseThrow(() -> new GameNotFoundException(game.getNextGameId()));

        var seat = game.takeRematchSeat(player, nextGame);
        this.gameRepository.saveGame(game);

        return toResponse(nextGame, toPlayerResponse(seat));
    }

    private static GameResponse toResponse(Game game, PlayerResponse myPlayer) {
        return new GameResponse(game.getExternalId(),
                game.getCurrentPlayer().getFigure(),
                myPlayer,
                game.getGamePhase(),
                game.getGameStatus(),
                game.getBoard().calculateState(),
                game.getRematchAskedBy(),
                game.getNextGameId());
    }

    private static PlayerResponse toPlayerResponse(Player player) {
        return new PlayerResponse(player.getExternalId(), player.getFigure().getStringFromFigure());
    }

    private boolean requesterIsPlayerOne(GameRequest request, PlayerType one, PlayerType two) {
        if (bothPlayersAreHuman(one, two)) {
            return request.myFigure().equals(Figure.X);
        }
        return PlayerType.isHuman(one);
    }

    private boolean bothPlayersAreHuman(PlayerType one, PlayerType two) {
        return PlayerType.isHuman(one) && PlayerType.isHuman(two);
    }

    private boolean oneOfThePlayersIsHuman(PlayerType one, PlayerType two) {
        return PlayerType.isHuman(one) || PlayerType.isHuman(two);
    }
}
