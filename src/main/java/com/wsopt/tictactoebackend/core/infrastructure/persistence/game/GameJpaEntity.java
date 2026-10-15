package com.wsopt.tictactoebackend.core.infrastructure.persistence.game;

import com.wsopt.tictactoebackend.core.domain.game.GamePhase;
import com.wsopt.tictactoebackend.core.domain.game.GameStatus;
import com.wsopt.tictactoebackend.core.infrastructure.persistence.board.BoardJpaEntity;
import com.wsopt.tictactoebackend.core.infrastructure.persistence.player.PlayerJpaEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "games")
public class GameJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "games_seq")
    @SequenceGenerator(name = "games_seq", sequenceName = "games_seq", allocationSize = 1)
    private Long id;

    @NotNull
    private UUID externalId;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "player_one_id")
    private PlayerJpaEntity playerOne;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "player_two_id")
    private PlayerJpaEntity playerTwo;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "board_id")
    private BoardJpaEntity board;

    @NotNull
    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    private GamePhase phase;

    @ManyToOne
    @JoinColumn(name = "current_player_id")
    private PlayerJpaEntity currentPlayer;

    private UUID nextGameExternalId;

    public GameJpaEntity() {
    }

    private GameJpaEntity(Builder builder) {
        id = builder.id;
        externalId = builder.externalId;
        playerOne = builder.playerOne;
        playerTwo = builder.playerTwo;
        board = builder.board;
        status = builder.status;
        phase = builder.phase;
        currentPlayer = builder.currentPlayer;
        nextGameExternalId = builder.nextGameExternalId;
    }

    public static Builder newGameJpaEntity() {
        return new Builder();
    }

    public UUID getExternalId() {
        return externalId;
    }

    public PlayerJpaEntity getPlayerOne() {
        return playerOne;
    }

    public PlayerJpaEntity getPlayerTwo() {
        return playerTwo;
    }

    public BoardJpaEntity getBoard() {
        return board;
    }

    public GameStatus getStatus() {
        return status;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public PlayerJpaEntity getCurrentPlayer() {
        return currentPlayer;
    }

    public UUID getNextGameExternalId() {
        return nextGameExternalId;
    }

    public void setNextGameExternalId(UUID nextGameExternalId) {
        this.nextGameExternalId = nextGameExternalId;
    }

    public void setPlayerOne(PlayerJpaEntity playerOne) {
        this.playerOne = playerOne;
    }

    public void setPlayerTwo(PlayerJpaEntity playerTwo) {
        this.playerTwo = playerTwo;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    public void setCurrentPlayer(PlayerJpaEntity currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public static final class Builder {
        private Long id;
        private @NotNull UUID externalId;
        private PlayerJpaEntity playerOne;
        private PlayerJpaEntity playerTwo;
        private BoardJpaEntity board;
        private @NotNull GameStatus status;
        private @NotNull GamePhase phase;
        private PlayerJpaEntity currentPlayer;
        private UUID nextGameExternalId;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder externalId(@NotNull UUID externalId) {
            this.externalId = externalId;
            return this;
        }

        public Builder playerOne(PlayerJpaEntity playerOne) {
            this.playerOne = playerOne;
            return this;
        }

        public Builder playerTwo(PlayerJpaEntity playerTwo) {
            this.playerTwo = playerTwo;
            return this;
        }

        public Builder board(BoardJpaEntity board) {
            this.board = board;
            return this;
        }

        public Builder status(@NotNull GameStatus status) {
            this.status = status;
            return this;
        }

        public Builder phase(@NotNull GamePhase phase) {
            this.phase = phase;
            return this;
        }

        public Builder currentPlayer(PlayerJpaEntity currentPlayer) {
            this.currentPlayer = currentPlayer;
            return this;
        }

        public Builder nextGameExternalId(UUID nextGameExternalId) {
            this.nextGameExternalId = nextGameExternalId;
            return this;
        }

        public GameJpaEntity build() {
            return new GameJpaEntity(this);
        }
    }
}
