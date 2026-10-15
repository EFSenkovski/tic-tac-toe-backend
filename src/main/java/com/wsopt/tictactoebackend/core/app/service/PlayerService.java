package com.wsopt.tictactoebackend.core.app.service;

import com.wsopt.tictactoebackend.core.app.port.in.CreatePlayerUseCase;
import com.wsopt.tictactoebackend.core.domain.game.Figure;
import com.wsopt.tictactoebackend.core.domain.game.player.Player;
import com.wsopt.tictactoebackend.core.domain.game.player.PlayerFactory;
import org.springframework.stereotype.Service;

@Service
public class PlayerService implements CreatePlayerUseCase {

    @Override
    public Player createPlayer(String input, Figure figure) {
        return PlayerFactory.buildPlayer(input, figure);
    }
}
