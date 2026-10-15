package com.wsopt.tictactoebackend.core.domain.game.player;

public enum PlayerType {
    USER("user"),
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard");

    private final String input;

    PlayerType(String input) {
        this.input = input;
    }

    public static PlayerType from(String input) {
        for (PlayerType type : values()) {
            if (type.input.equals(input)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Player level does not exist: " + input);
    }

    public boolean isHuman() {
        return this == USER;
    }

    public static boolean isHuman(PlayerType type) {
        return type == USER;
    }
}
