package com.wsopt.tictactoebackend.core.domain.game;

import com.wsopt.tictactoebackend.core.domain.exception.DomainException;
import com.wsopt.tictactoebackend.core.domain.game.player.Player;
import com.wsopt.tictactoebackend.core.domain.game.player.PlayerFactory;
import com.wsopt.tictactoebackend.core.domain.game.player.PlayerType;
import com.wsopt.tictactoebackend.core.domain.game.player.Rematch;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class GameTest {

    @Test
    void given_PlayerNotInGame_WhenMakingMove_ThenShouldThrowInvalidPlayer() {
        var playerOne = new Player(Figure.X, PlayerType.USER);
        var playerTwo = new Player(Figure.O, PlayerType.USER);
        var game = new Game(playerOne, playerTwo);

        var outsider = new Player(Figure.X, PlayerType.USER);

        assertThatThrownBy(() -> game.makeMove(0, outsider))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Invalid player");
        assertThat(game.isFigureFilled(0)).isFalse();
    }

    @Test
    void given_WinningMove_WhenMakingMove_ThenPhaseShouldBeFinished() {
        var playerOne = new Player(Figure.X, PlayerType.USER);
        var playerTwo = new Player(Figure.O, PlayerType.USER);
        var game = new Game(playerOne, playerTwo);

        game.makeMove(0, playerOne);
        game.makeMove(3, playerTwo);
        game.makeMove(1, playerOne);
        game.makeMove(4, playerTwo);
        assertThat(game.getGamePhase()).isEqualTo(GamePhase.PLAYING);

        game.makeMove(2, playerOne);

        assertThat(game.getGameStatus()).isEqualTo(GameStatus.XWINS);
        assertThat(game.getGamePhase()).isEqualTo(GamePhase.FINISHED);
    }

    @Test
    void given_FinishedGame_WhenMakingMove_ThenShouldThrow() {
        var playerOne = new Player(Figure.X, PlayerType.USER);
        var playerTwo = new Player(Figure.O, PlayerType.USER);
        var game = new Game(playerOne, playerTwo);
        game.makeMove(0, playerOne);
        game.makeMove(3, playerTwo);
        game.makeMove(1, playerOne);
        game.makeMove(4, playerTwo);
        game.makeMove(2, playerOne);

        assertThatThrownBy(() -> game.makeMove(5, playerTwo))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("not in progress");
        assertThat(game.isFigureFilled(5)).isFalse();
    }

    @Test
    void given_GameWaitingForSecondPlayer_WhenMakingMove_ThenShouldThrow() {
        var playerOne = new Player(Figure.X, PlayerType.USER);
        var game = new Game(playerOne, null);

        assertThatThrownBy(() -> game.makeMove(0, playerOne))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("not in progress");
    }

    @Test
    void given_HumanWinsAgainstAi_WhenMakingMove_ThenAiShouldNotMoveAndPhaseShouldBeFinished() {
        var human = new Player(Figure.X, PlayerType.USER);
        var ai = PlayerFactory.rebuildPlayer(Figure.O, UUID.randomUUID(), PlayerType.HARD, null, Rematch.NOT_ASKED);
        var game = new Game(UUID.randomUUID(), new Board("XX_OO____", UUID.randomUUID()),
                GameStatus.NOT_FINISHED, GamePhase.PLAYING, human, ai, human, null);

        game.makeMove(2, human);

        assertThat(game.getGameStatus()).isEqualTo(GameStatus.XWINS);
        assertThat(game.getGamePhase()).isEqualTo(GamePhase.FINISHED);
        assertThat(game.getBoard().calculateState()).isEqualTo("XXXOO____");
    }

    @Test
    void given_PlayerInGame_WhenMakingMove_ThenShouldPlaceFigure() {
        var playerOne = new Player(Figure.X, PlayerType.USER);
        var playerTwo = new Player(Figure.O, PlayerType.USER);
        var game = new Game(playerOne, playerTwo);

        game.makeMove(4, playerOne);

        assertThat(game.snapshot().get(4)).isEqualTo(Figure.X);
        assertThat(game.getCurrentPlayer()).isSameAs(playerTwo);
    }

    @Test
    void given_PlayerTwoSeatEmpty_WhenSettingMissingPlayer_ThenShouldFillPlayerTwoAndKeepPlayerOne() {
        var playerOne = new Player(Figure.X, PlayerType.USER);
        var game = new Game(playerOne, null);

        var newcomer = new Player(Figure.O, PlayerType.USER);
        game.setMissingPlayer(newcomer);

        assertThat(game.getPlayerOne()).isSameAs(playerOne);
        assertThat(game.getPlayerTwo()).isSameAs(newcomer);
        assertThat(game.areTwoPlayerSeatsTaken()).isTrue();
    }

    @Test
    void given_PlayerOneSeatEmpty_WhenSettingMissingPlayer_ThenShouldFillPlayerOneAndKeepPlayerTwo() {
        var playerTwo = new Player(Figure.O, PlayerType.USER);
        var game = new Game(null, playerTwo);

        var newcomer = new Player(Figure.X, PlayerType.USER);
        game.setMissingPlayer(newcomer);

        assertThat(game.getPlayerOne()).isSameAs(newcomer);
        assertThat(game.getPlayerTwo()).isSameAs(playerTwo);
        assertThat(game.areTwoPlayerSeatsTaken()).isTrue();
    }
}
