package com.wsopt.tictactoebackend.core.app.port.in.game.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MakeMoveRequest(
        @NotNull UUID gameId,
        @NotNull UUID playerId,
        @NotNull @Min(0) @Max(8) Integer index) {
}
