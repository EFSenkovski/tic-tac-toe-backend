package com.wsopt.tictactoebackend.core.domain.game.strategy;

import com.wsopt.tictactoebackend.core.domain.game.Figure;
import com.wsopt.tictactoebackend.core.domain.game.Game;
import com.wsopt.tictactoebackend.core.domain.game.GameStatus;

import java.util.List;

public class MediumMoveStrategy implements MoveStrategy {

    private final MoveStrategy fallback = new EasyMoveStrategy();

    @Override
    public int getNextMoveIndex(Game game) {
        Figure playerFigure = game.getCurrentPlayer().getFigure();
        List<Figure> cells = game.snapshot();

        Integer winningMove = findWinningMove(cells, playerFigure);
        if (winningMove != null) return winningMove;

        Integer blockingMove = findWinningMove(cells, playerFigure.opposite());
        if (blockingMove != null) return blockingMove;

        return fallback.getNextMoveIndex(game);
    }

    private Integer findWinningMove(List<Figure> cells, Figure figure) {
        for (int index = 0; index < cells.size(); index++) {
            if (cells.get(index) == null) {
                GameStatus gameStatus = makeMove(cells, index, figure);
                if (isWinningMove(figure, gameStatus)) return index;
            }
        }
        return null;
    }

    private GameStatus makeMove(List<Figure> cells, int index, Figure figure) {
        cells.set(index, figure);
        GameStatus status = Game.calculateGameStatus(cells);
        cells.set(index, null);
        return status;
    }

    private boolean isWinningMove(Figure figure, GameStatus status) {
        if (figure.equals(Figure.X)) return status.equals(GameStatus.XWINS);
        if (figure.equals(Figure.O)) return status.equals(GameStatus.OWINS);
        return false;
    }
}
