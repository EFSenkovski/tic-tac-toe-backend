package com.wsopt.tictactoebackend.core.app.port.in.game;

import com.wsopt.tictactoebackend.core.app.port.in.game.request.MakeMoveRequest;
import com.wsopt.tictactoebackend.core.app.port.in.game.response.GameResponse;


public interface MakeMoveUseCase {
    GameResponse makeMove(MakeMoveRequest request);
}
