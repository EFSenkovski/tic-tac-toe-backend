package com.wsopt.tictactoebackend.core.domain.game.board;

import com.wsopt.tictactoebackend.core.domain.game.Board;
import com.wsopt.tictactoebackend.core.domain.game.Figure;
import com.wsopt.tictactoebackend.core.domain.game.Game;
import com.wsopt.tictactoebackend.core.domain.game.player.Player;
import com.wsopt.tictactoebackend.core.domain.game.player.PlayerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class BoardTest {

    Board board;
    private static final int VALID_INDEX = 2;
    private static final int ANOTHER_VALID_INDEX = 5;
    private static final int INVALID_INDEX = 10;


    @BeforeEach
    void setUp() {
        board = new Game(new Player(Figure.X, PlayerType.USER),
                new Player(Figure.O, PlayerType.USER)).getBoard();
    }

    @Test
    void given_ValidIndex_WhenAddingToBoard_ThenShouldSet() {
        board.addToBoard(VALID_INDEX, Figure.X);

        assertThat(board.isFigureFilled(VALID_INDEX)).isTrue();
    }

    @Test
    void given_InvalidIndex_WhenAddingToBoard_ThenShouldThrowException() {
        assertThrows(IndexOutOfBoundsException.class,
                () -> board.addToBoard(INVALID_INDEX, Figure.O));

    }

    @Test
    void given_AFilledBoard_WhenSnapshot_ShouldReturnAConsistentSnapshot() {
        var figureX = Figure.X;
        var figureO = Figure.O;
        board.addToBoard(VALID_INDEX, figureX);
        board.addToBoard(ANOTHER_VALID_INDEX, figureO);

        var snapshot = board.snapshot();

        assertThat(snapshot.get(VALID_INDEX)).isEqualTo(figureX);
        assertThat(snapshot.get(ANOTHER_VALID_INDEX)).isEqualTo(figureO);
        assertThat(board.isFigureFilled(VALID_INDEX)).isTrue();
        assertThat(board.isFigureFilled(ANOTHER_VALID_INDEX)).isTrue();
    }
}