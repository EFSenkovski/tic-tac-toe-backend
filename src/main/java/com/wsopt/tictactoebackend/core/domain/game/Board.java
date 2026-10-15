package com.wsopt.tictactoebackend.core.domain.game;

import com.wsopt.tictactoebackend.core.domain.exception.DomainException;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.UUID;

public class Board {
    private static final int SIZE = 9;
    private final List<Figure> cells;
    private final UUID externalId;

    public Board() {
        this.cells = new ArrayList<>(Collections.nCopies(SIZE, null));
        this.externalId = UUID.randomUUID();
    }

    public Board(String state, UUID externalId) {
        this.externalId = externalId;
        this.cells = new ArrayList<>(Collections.nCopies(SIZE, null));
        for (int i = 0; i < SIZE; i++) {
            var currentFigure = state.substring(i, i+1);
            Figure figure = Figure.from(currentFigure);
            if (figure != null) {
                this.cells.set(i, figure);
            }
        }
    }

    public void addToBoard(int index, Figure figure) {
        if (this.cells.get(index) != null) {
            throw new DomainException("Cell is filled up, you need to choose another");
        }
        this.cells.set(index, figure);
    }

    public List<Figure> snapshot() {
        return new ArrayList<>(cells);
    }

    public boolean isFigureFilled(int index) {
        return this.cells.get(index) != null;
    }

    public String calculateState() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Figure figure : cells) {
            String currentState = figure == null ? "_" : figure.getStringFromFigure();
            stringBuilder.append(currentState);
        }
        return stringBuilder.toString();
    }

    public UUID getExternalId() {
        return externalId;
    }
}
