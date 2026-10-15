package com.wsopt.tictactoebackend.core.domain.game.player;

/**
 * How far a Player of a finished Game has got with a Rematch.
 */
public enum Rematch {
    /** Has not asked for a Rematch. */
    NOT_ASKED,
    /** Has asked; waiting for the other Player to ask too, or to be handed their seat in the next Game. */
    ASKED,
    /** Has been handed their seat in the next Game. */
    SEATED
}
