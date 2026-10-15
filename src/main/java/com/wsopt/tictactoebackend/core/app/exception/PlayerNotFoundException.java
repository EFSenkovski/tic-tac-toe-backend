package com.wsopt.tictactoebackend.core.app.exception;

import java.util.UUID;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(UUID playerId) {
        super(String.format("Player %s does not exist!", playerId));
    }
}
