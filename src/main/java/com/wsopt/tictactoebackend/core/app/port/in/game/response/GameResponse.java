package com.wsopt.tictactoebackend.core.app.port.in.game.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wsopt.tictactoebackend.core.app.port.in.shared.PlayerResponse;
import com.wsopt.tictactoebackend.core.domain.game.Figure;
import com.wsopt.tictactoebackend.core.domain.game.GamePhase;
import com.wsopt.tictactoebackend.core.domain.game.GameStatus;

import java.util.List;
import java.util.UUID;

public record GameResponse(UUID gameId,
                           Figure currentFigure,
                           @JsonInclude(JsonInclude.Include.NON_NULL) PlayerResponse myPlayer,
                           GamePhase phase,
                           GameStatus status,
                           String board,
                           List<Figure> rematchAskedBy,
                           UUID nextGameId) {
}
