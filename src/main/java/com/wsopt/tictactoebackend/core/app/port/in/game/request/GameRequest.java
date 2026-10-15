package com.wsopt.tictactoebackend.core.app.port.in.game.request;

import com.wsopt.tictactoebackend.core.domain.game.Figure;

public record GameRequest(String playerOne, String playerTwo, Figure myFigure) {
}
