package com.wsopt.tictactoebackend.core.app.port.in.game;

import com.wsopt.tictactoebackend.core.app.port.in.game.response.GameResponse;

import java.util.UUID;

public interface GetGameUseCase {
    GameResponse getGame(UUID gameId);
}
