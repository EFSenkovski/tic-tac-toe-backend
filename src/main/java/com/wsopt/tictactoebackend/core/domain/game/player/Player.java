package com.wsopt.tictactoebackend.core.domain.game.player;

import com.wsopt.tictactoebackend.core.domain.game.Figure;

import java.util.UUID;

public class Player {

    protected final Figure figure;
    protected final UUID externalId;
    private final PlayerType playerType;
    private UUID userExternalId;
    private Rematch rematch;

    public Player(Figure figure, PlayerType playerType) {
        this.figure = figure;
        this.playerType = playerType;
        this.externalId = UUID.randomUUID();
        this.rematch = Rematch.NOT_ASKED;
    }

    public Player(Figure figure, UUID externalId, PlayerType playerType, UUID userExternalId, Rematch rematch) {
        this.figure = figure;
        this.externalId = externalId;
        this.playerType = playerType;
        this.userExternalId = userExternalId;
        this.rematch = rematch;
    }

    public Figure getFigure() {
        return figure;
    }

    public UUID getExternalId() {
        return externalId;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public UUID getUserExternalId() {
        return userExternalId;
    }

    public void setUserExternalId(UUID userExternalId) {
        this.userExternalId = userExternalId;
    }

    public Rematch getRematch() {
        return rematch;
    }

    public void askForRematch() {
        if (rematch == Rematch.NOT_ASKED) {
            rematch = Rematch.ASKED;
        }
    }

    public void takeRematchSeat() {
        rematch = Rematch.SEATED;
    }
}
