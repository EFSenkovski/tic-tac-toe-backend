package com.wsopt.tictactoebackend.core.app.port.in.game.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TakePlayerSeatRequest(@NotNull UUID gameId) {
}
