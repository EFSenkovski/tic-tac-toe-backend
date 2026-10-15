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

class GameRematchTest {

    private final Player x = new Player(Figure.X, PlayerType.USER);
    private final Player o = new Player(Figure.O, PlayerType.USER);

    @Test
    void given_FinishedGame_WhenOnePlayerAsks_ThenRematchShouldNotBeAgreedYet() {
        var game = finished(x, o);

        game.askForRematch(x);

        assertThat(game.getRematchAskedBy()).containsExactly(Figure.X);
        assertThat(game.isRematchAgreed()).isFalse();
        assertThat(game.getNextGameId()).isNull();
    }

    @Test
    void given_PlayerAlreadyAsked_WhenAskingAgain_ThenNothingShouldChange() {
        var game = finished(x, o);
        game.askForRematch(x);

        game.askForRematch(x);

        assertThat(game.getRematchAskedBy()).containsExactly(Figure.X);
        assertThat(x.getRematch()).isEqualTo(Rematch.ASKED);
    }

    @Test
    void given_BothHumansAsked_WhenRematching_ThenNextGameShouldSwapFiguresAndBePlaying() {
        var game = finished(x, o);
        game.askForRematch(o);
        game.askForRematch(x);

        var next = game.rematch();

        assertThat(game.isRematchAgreed()).isTrue();
        assertThat(game.getNextGameId()).isEqualTo(next.getExternalId());
        assertThat(next.getGamePhase()).isEqualTo(GamePhase.PLAYING);
        assertThat(next.getGameStatus()).isEqualTo(GameStatus.NOT_FINISHED);
        assertThat(next.getBoard().calculateState()).isEqualTo("_________");
        assertThat(next.getCurrentPlayer().getFigure()).isEqualTo(Figure.X);
        assertThat(next.getRematchAskedBy()).isEmpty();
    }

    @Test
    void given_RematchCreated_WhenEachPlayerTakesTheirSeat_ThenTheyShouldGetTheOppositeFigure() {
        var game = finished(x, o);
        game.askForRematch(x);
        game.askForRematch(o);
        var next = game.rematch();

        var formerX = game.takeRematchSeat(x, next);
        var formerO = game.takeRematchSeat(o, next);

        assertThat(formerX.getFigure()).isEqualTo(Figure.O);
        assertThat(formerO.getFigure()).isEqualTo(Figure.X);
        assertThat(formerX.getExternalId()).isNotIn(x.getExternalId(), o.getExternalId(), formerO.getExternalId());
        assertThat(x.getRematch()).isEqualTo(Rematch.SEATED);
        assertThat(o.getRematch()).isEqualTo(Rematch.SEATED);
    }

    @Test
    void given_PlayerAlreadySeated_WhenTakingSeatAgain_ThenShouldThrow() {
        var game = finished(x, o);
        game.askForRematch(x);
        game.askForRematch(o);
        var next = game.rematch();
        game.takeRematchSeat(x, next);

        assertThatThrownBy(() -> game.takeRematchSeat(x, next))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("no rematch seat waiting");
    }

    @Test
    void given_PlayerAlreadySeated_WhenAskingAgain_ThenShouldThrow() {
        var game = finished(x, o);
        game.askForRematch(x);
        game.askForRematch(o);
        game.takeRematchSeat(x, game.rematch());

        assertThatThrownBy(() -> game.askForRematch(x))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("already taken their seat");
    }

    @Test
    void given_HumanPlayedXAgainstMachine_WhenHumanAsks_ThenMachineShouldOpenTheNextGameAsX() {
        var machine = PlayerFactory.rebuildPlayer(Figure.O, UUID.randomUUID(), PlayerType.MEDIUM, null, Rematch.NOT_ASKED);
        var game = finished(x, machine);

        game.askForRematch(x);
        var next = game.rematch();
        var seat = game.takeRematchSeat(x, next);

        assertThat(next.getPlayerOne().getPlayerType()).isEqualTo(PlayerType.MEDIUM);
        assertThat(next.getPlayerTwo().getPlayerType()).isEqualTo(PlayerType.USER);
        assertThat(seat.getFigure()).isEqualTo(Figure.O);
        assertThat(next.getBoard().calculateState().chars().filter(c -> c == 'X').count()).isEqualTo(1);
        assertThat(next.getCurrentPlayer().getFigure()).isEqualTo(Figure.O);
    }

    @Test
    void given_HumanPlayedOAgainstMachine_WhenHumanAsks_ThenHumanShouldOpenTheNextGameAsX() {
        var machine = PlayerFactory.rebuildPlayer(Figure.X, UUID.randomUUID(), PlayerType.HARD, null, Rematch.NOT_ASKED);
        var game = finished(machine, o);

        game.askForRematch(o);
        assertThat(game.isRematchAgreed()).isTrue();
        var next = game.rematch();
        var seat = game.takeRematchSeat(o, next);

        assertThat(seat.getFigure()).isEqualTo(Figure.X);
        assertThat(next.getPlayerTwo().getPlayerType()).isEqualTo(PlayerType.HARD);
        assertThat(next.getBoard().calculateState()).isEqualTo("_________");
        assertThat(next.getCurrentPlayer().getFigure()).isEqualTo(Figure.X);
    }

    @Test
    void given_GameStillPlaying_WhenAskingForRematch_ThenShouldThrowNotOver() {
        var game = new Game(x, o);

        assertThatThrownBy(() -> game.askForRematch(x))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("is not over");
    }

    @Test
    void given_PlayerNotInGame_WhenAskingForRematch_ThenShouldThrowInvalidPlayer() {
        var game = finished(x, o);

        assertThatThrownBy(() -> game.askForRematch(new Player(Figure.X, PlayerType.USER)))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Invalid player");
        assertThat(game.getRematchAskedBy()).isEmpty();
    }

    @Test
    void given_OnlyOneHumanAsked_WhenRematching_ThenShouldThrow() {
        var game = finished(x, o);
        game.askForRematch(x);

        assertThatThrownBy(game::rematch)
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Not every player");
        assertThat(game.getNextGameId()).isNull();
    }

    @Test
    void given_RematchAlreadyCreated_WhenRematchingAgain_ThenShouldThrow() {
        var game = finished(x, o);
        game.askForRematch(x);
        game.askForRematch(o);
        var next = game.rematch();

        assertThatThrownBy(game::rematch)
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("already has a rematch");
        assertThat(game.getNextGameId()).isEqualTo(next.getExternalId());
    }

    @Test
    void given_UnrelatedGame_WhenTakingRematchSeat_ThenShouldThrow() {
        var game = finished(x, o);
        game.askForRematch(x);
        game.askForRematch(o);
        game.rematch();
        var unrelated = new Game(new Player(Figure.X, PlayerType.USER), new Player(Figure.O, PlayerType.USER));

        assertThatThrownBy(() -> game.takeRematchSeat(x, unrelated))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("is not the rematch");
        assertThat(x.getRematch()).isEqualTo(Rematch.ASKED);
    }

    private static Game finished(Player playerOne, Player playerTwo) {
        return new Game(UUID.randomUUID(), new Board("XXXOO____", UUID.randomUUID()),
                GameStatus.XWINS, GamePhase.FINISHED, playerOne, playerTwo, playerTwo, null);
    }
}
