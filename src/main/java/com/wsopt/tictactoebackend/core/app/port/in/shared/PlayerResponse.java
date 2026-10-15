package com.wsopt.tictactoebackend.core.app.port.in.shared;

import java.util.UUID;

public record PlayerResponse(UUID playerId, String figure) {

}
