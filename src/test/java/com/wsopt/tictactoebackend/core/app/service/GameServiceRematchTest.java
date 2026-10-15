package com.wsopt.tictactoebackend.core.app.service;

import com.wsopt.tictactoebackend.core.app.exception.ApplicationException;
import com.wsopt.tictactoebackend.core.app.exception.GameNotFoundException;
import com.wsopt.tictactoebackend.core.app.exception.PlayerNotFoundException;
import com.wsopt.tictactoebackend.core.app.port.in.game.request.GameRequest;
import com.wsopt.tictactoebackend.core.app.port.in.game.request.MakeMoveRequest;
import com.wsopt.tictactoebackend.core.app.port.in.game.request.RematchRequest;
import com.wsopt.tictactoebackend.core.app.port.out.GameRepository;
import com.wsopt.tictactoebackend.core.domain.exception.DomainException;
import com.wsopt.tictactoebackend.core.domain.game.Board;
import com.wsopt.tictactoebackend.core.domain.game.Figure;
import com.wsopt.tictactoebackend.core.domain.game.Game;
import com.wsopt.tictactoebackend.core.domain.game.GamePhase;
import com.wsopt.tictactoebackend.core.domain.game.GameStatus;
import com.wsopt.tictactoebackend.core.domain.game.player.Player;
import com.wsopt.tictactoebackend.core.domain.game.player.PlayerFactory;
import com.wsopt.tictactoebackend.core.domain.game.player.PlayerType;
import com.wsopt.tictactoebackend.core.domain.game.player.Rematch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Runs the Rematch through the service and the real database; every test rolls back.
 */
@SpringBootTest
@Transactional
class GameServiceRematchTest {

    @Autowired
    GameService gameService;
    @Autowired
    GameRepository gameRepository;

    @Test
    void given_FinishedHumanGame_WhenFirstPlayerAsks_ThenFinishedGameShouldShowTheRequestOnly() {
        var x = new Player(Figure.X, PlayerType.USER);
        var o = new Player(Figure.O, PlayerType.USER);
        var gameId = saveFinished(x, o);

        var response = gameService.rematch(new RematchRequest(gameId, x.getExternalId()));

        assertThat(response.gameId()).isEqualTo(gameId);
        assertThat(response.myPlayer()).isNull();
        assertThat(response.rematchAskedBy()).containsExactly(Figure.X);
        assertThat(response.nextGameId()).isNull();
        assertThat(gameService.getGame(gameId).rematchAskedBy()).containsExactly(Figure.X);
    }

    @Test
    void given_BothHumansAsk_WhenEachCallsRematch_ThenEachShouldGetTheirSwappedSeatOnce() {
        var x = new Player(Figure.X, PlayerType.USER);
        var o = new Player(Figure.O, PlayerType.USER);
        var gameId = saveFinished(x, o);

        gameService.rematch(new RematchRequest(gameId, x.getExternalId()));
        var forO = gameService.rematch(new RematchRequest(gameId, o.getExternalId()));

        assertThat(forO.gameId()).isNotEqualTo(gameId);
        assertThat(forO.phase()).isEqualTo(GamePhase.PLAYING);
        assertThat(forO.currentFigure()).isEqualTo(Figure.X);
        assertThat(forO.myPlayer().figure()).isEqualTo("X");
        var finished = gameService.getGame(gameId);
        assertThat(finished.nextGameId()).isEqualTo(forO.gameId());
        assertThat(finished.rematchAskedBy()).containsExactlyInAnyOrder(Figure.X, Figure.O);
        assertThat(finished.myPlayer()).isNull();

        var forX = gameService.rematch(new RematchRequest(gameId, x.getExternalId()));

        assertThat(forX.gameId()).isEqualTo(forO.gameId());
        assertThat(forX.myPlayer().figure()).isEqualTo("O");
        assertThat(forX.myPlayer().playerId()).isNotEqualTo(forO.myPlayer().playerId());
        assertThatThrownBy(() -> gameService.rematch(new RematchRequest(gameId, x.getExternalId())))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("already taken their seat");
        assertThatThrownBy(() -> gameService.rematch(new RematchRequest(gameId, o.getExternalId())))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("already taken their seat");
    }

    @Test
    void given_RematchAgreed_WhenPlayingTheNextGame_ThenOnlyTheNewPlayerIdsShouldMove() {
        var x = new Player(Figure.X, PlayerType.USER);
        var o = new Player(Figure.O, PlayerType.USER);
        var gameId = saveFinished(x, o);
        gameService.rematch(new RematchRequest(gameId, x.getExternalId()));
        var forO = gameService.rematch(new RematchRequest(gameId, o.getExternalId()));
        var nextId = forO.gameId();

        assertThatThrownBy(() -> gameService.makeMove(new MakeMoveRequest(nextId, o.getExternalId(), 0)))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Invalid player");

        var moved = gameService.makeMove(new MakeMoveRequest(nextId, forO.myPlayer().playerId(), 4));

        assertThat(moved.board()).isEqualTo("____X____");
        assertThat(moved.currentFigure()).isEqualTo(Figure.O);
    }

    @Test
    void given_HumanPlayedXAgainstMachine_WhenHumanAsks_ThenNextGameShouldStartAtOnceWithTheMachineOpening() {
        var x = new Player(Figure.X, PlayerType.USER);
        var machine = PlayerFactory.rebuildPlayer(Figure.O, UUID.randomUUID(), PlayerType.EASY, null, Rematch.NOT_ASKED);
        var gameId = saveFinished(x, machine);

        var response = gameService.rematch(new RematchRequest(gameId, x.getExternalId()));

        assertThat(response.gameId()).isNotEqualTo(gameId);
        assertThat(response.phase()).isEqualTo(GamePhase.PLAYING);
        assertThat(response.myPlayer().figure()).isEqualTo("O");
        assertThat(response.currentFigure()).isEqualTo(Figure.O);
        assertThat(response.board().chars().filter(c -> c == 'X').count()).isEqualTo(1);
        assertThat(gameService.getGame(gameId).nextGameId()).isEqualTo(response.gameId());
    }

    @Test
    void given_RematchAgainstMachine_WhenRematchingTheRematch_ThenFiguresShouldSwapBack() {
        var x = new Player(Figure.X, PlayerType.USER);
        var machine = PlayerFactory.rebuildPlayer(Figure.O, UUID.randomUUID(), PlayerType.HARD, null, Rematch.NOT_ASKED);
        var first = gameService.rematch(new RematchRequest(saveFinished(x, machine), x.getExternalId()));
        var next = gameRepository.findByExternalId(first.gameId()).orElseThrow();
        var finishedNext = new Game(next.getExternalId(), new Board("XXXOO____", UUID.randomUUID()),
                GameStatus.XWINS, GamePhase.FINISHED, next.getPlayerOne(), next.getPlayerTwo(), next.getPlayerTwo(), null);
        gameRepository.saveGame(finishedNext);

        var second = gameService.rematch(new RematchRequest(first.gameId(), first.myPlayer().playerId()));

        assertThat(second.myPlayer().figure()).isEqualTo("X");
        assertThat(second.board()).isEqualTo("_________");
    }

    @Test
    void given_GameStillPlaying_WhenAskingForRematch_ThenShouldBeRefusedAsNotOver() {
        var created = gameService.createGame(new GameRequest("user", "user", Figure.X));

        assertThatThrownBy(() -> gameService.rematch(new RematchRequest(created.gameId(), created.myPlayer().playerId())))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("is not over");
    }

    @Test
    void given_UnknownGameOrPlayer_WhenAskingForRematch_ThenShouldBeNotFound() {
        var x = new Player(Figure.X, PlayerType.USER);
        var gameId = saveFinished(x, new Player(Figure.O, PlayerType.USER));

        assertThatThrownBy(() -> gameService.rematch(new RematchRequest(UUID.randomUUID(), x.getExternalId())))
                .isInstanceOf(GameNotFoundException.class);
        assertThatThrownBy(() -> gameService.rematch(new RematchRequest(gameId, UUID.randomUUID())))
                .isInstanceOf(PlayerNotFoundException.class);
    }

    @Test
    void given_PlayerOfAnotherGame_WhenAskingForRematch_ThenShouldBeRefusedAsInvalidPlayer() {
        var gameId = saveFinished(new Player(Figure.X, PlayerType.USER), new Player(Figure.O, PlayerType.USER));
        var outsider = new Player(Figure.X, PlayerType.USER);
        saveFinished(outsider, new Player(Figure.O, PlayerType.USER));

        assertThatThrownBy(() -> gameService.rematch(new RematchRequest(gameId, outsider.getExternalId())))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Invalid player");
        assertThat(gameService.getGame(gameId).rematchAskedBy()).isEmpty();
    }

    private UUID saveFinished(Player playerOne, Player playerTwo) {
        var game = new Game(UUID.randomUUID(), new Board("XXXOO____", UUID.randomUUID()),
                GameStatus.XWINS, GamePhase.FINISHED, playerOne, playerTwo, playerTwo, null);
        return gameRepository.saveGame(game).getExternalId();
    }
}
