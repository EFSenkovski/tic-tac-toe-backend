package com.wsopt.tictactoebackend.core.domain.game.strategy;

import com.wsopt.tictactoebackend.core.domain.game.Game;

import java.util.Random;

public class EasyMoveStrategy implements MoveStrategy{
    @Override
    public int getNextMoveIndex(Game game) {
        Random random = new Random();
        boolean cellIsFilled;
        int index;
        do {
            index = random.nextInt(9);
            cellIsFilled = game.isFigureFilled(index);
        } while (cellIsFilled);

        return index;
    }
}
