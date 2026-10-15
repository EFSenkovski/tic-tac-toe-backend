package com.wsopt.tictactoebackend.core.app.port.in.game;

import com.wsopt.tictactoebackend.core.app.port.in.game.request.RematchRequest;
import com.wsopt.tictactoebackend.core.app.port.in.game.response.GameResponse;

public interface RematchUseCase {
    GameResponse rematch(RematchRequest request);
}
