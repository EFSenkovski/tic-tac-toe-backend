package com.wsopt.tictactoebackend.core.app.exception;

import java.util.UUID;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(UUID gameId) {
        super(String.format("Game %s does not exist!", gameId));
    }
}
