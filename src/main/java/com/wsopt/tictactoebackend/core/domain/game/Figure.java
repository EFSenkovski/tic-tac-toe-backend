package com.wsopt.tictactoebackend.core.domain.game;

public enum Figure {
    X,
    O;

    public Figure opposite() {
        return switch (this) {
            case X -> O;
            case O -> X;
        };
    }

    public static Figure from(String input) {
        return switch (input) {
            case "X" -> X;
            case "O" -> O;
            default -> null;
        };
    }

    public String getStringFromFigure() {
        return switch (this) {
            case O -> "O";
            case X -> "X";
        };
    }
}
