package com.wsopt.tictactoebackend.core.app.port.in.game;

import com.wsopt.tictactoebackend.core.app.port.in.game.response.GameResponse;

import java.util.UUID;

public interface TakePlayerSeatUseCase {
    GameResponse takePlayerSeat(UUID gameId);
}
