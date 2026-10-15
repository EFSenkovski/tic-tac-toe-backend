package com.wsopt.tictactoebackend.core.app.port.in.game;

import com.wsopt.tictactoebackend.core.app.port.in.game.request.GameRequest;
import com.wsopt.tictactoebackend.core.app.port.in.game.response.GameResponse;

public interface CreateGameUseCase {
    GameResponse createGame(GameRequest request);
}
