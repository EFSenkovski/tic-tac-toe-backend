package com.wsopt.tictactoebackend.core.domain.game.strategy;

import com.wsopt.tictactoebackend.core.domain.game.Figure;
import com.wsopt.tictactoebackend.core.domain.game.Game;
import com.wsopt.tictactoebackend.core.domain.game.GameStatus;

import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class HardMoveStrategy implements MoveStrategy {

    @Override
    public int getNextMoveIndex(Game game) {
        Figure aiFigure = game.getCurrentPlayer().getFigure();
        return findBestMove(game.snapshot(), aiFigure);
    }

    private int findBestMove(List<Figure> cells, Figure aiFigure) {
        int bestScore = Integer.MIN_VALUE;
        int bestIndex = -1;

        for (int index = 0; index < cells.size(); index++) {
            if (cells.get(index) == null) {
                cells.set(index, aiFigure);
                int score = minimax(cells, aiFigure.opposite(), aiFigure);
                cells.set(index, null);
                if (score > bestScore) {
                    bestScore = score;
                    bestIndex = index;
                }
            }
        }
        return bestIndex;
    }

    private int minimax(List<Figure> cells, Figure figure, Figure aiFigure) {
        GameStatus status = Game.calculateGameStatus(cells);
        if (!status.equals(GameStatus.NOT_FINISHED)) {
            return calculateScore(status, aiFigure);
        }

        boolean maxing = aiFigure.equals(figure);
        int best = maxing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int index = 0; index < cells.size(); index++) {
            if (cells.get(index) == null) {
                cells.set(index, figure);
                int score = minimax(cells, figure.opposite(), aiFigure);
                cells.set(index, null);
                best = maxing ? max(best, score) : min(best, score);
            }
        }
        return best;
    }

    private int calculateScore(GameStatus gameStatus, Figure aiFigure) {
        if (gameStatus.equals(GameStatus.NOT_FINISHED))
            throw new IllegalStateException("The game is not finished, so you can't try and calculate the score for the terminal state");
        if (gameStatus.equals(GameStatus.DRAW)) return 0;

        if ((aiFigure.equals(Figure.X) && gameStatus.equals(GameStatus.XWINS)) ||
                (aiFigure.equals(Figure.O) && gameStatus.equals(GameStatus.OWINS))) {
            return 10;
        } else {
            return -10;
        }
    }
}
