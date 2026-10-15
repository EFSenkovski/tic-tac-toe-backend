package com.wsopt.tictactoebackend.core.infrastructure.persistence.player;

import com.wsopt.tictactoebackend.core.domain.game.Figure;
import com.wsopt.tictactoebackend.core.domain.game.player.PlayerType;
import com.wsopt.tictactoebackend.core.domain.game.player.Rematch;
import com.wsopt.tictactoebackend.core.infrastructure.persistence.user.UserJpaEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "players")
public class PlayerJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "players_seq")
    @SequenceGenerator(name = "players_seq", sequenceName = "players_seq", allocationSize = 1)
    private Long id;

    @NotNull
    private UUID externalId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Figure figure;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PlayerType type;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserJpaEntity user;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Rematch rematch;

    public PlayerJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public UUID getExternalId() {
        return externalId;
    }

    public Figure getFigure() {
        return figure;
    }

    public PlayerType getType() {
        return type;
    }

    public UserJpaEntity getUser() {
        return user;
    }

    public Rematch getRematch() {
        return rematch;
    }

    public void setRematch(Rematch rematch) {
        this.rematch = rematch;
    }

    public static Builder newPlayerJpaEntity() {
        return new Builder();
    }

    private PlayerJpaEntity(Builder builder) {
        id = builder.id;
        externalId = builder.externalId;
        figure = builder.figure;
        type = builder.type;
        user = builder.user;
        rematch = builder.rematch;
    }

    public static final class Builder {
        private Long id;
        private @NotNull UUID externalId;
        private @NotNull Figure figure;
        private @NotNull PlayerType type;
        private UserJpaEntity user;
        private @NotNull Rematch rematch;

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

        public Builder figure(@NotNull Figure figure) {
            this.figure = figure;
            return this;
        }

        public Builder type(@NotNull PlayerType type) {
            this.type = type;
            return this;
        }

        public Builder user(UserJpaEntity user) {
            this.user = user;
            return this;
        }

        public Builder rematch(@NotNull Rematch rematch) {
            this.rematch = rematch;
            return this;
        }

        public PlayerJpaEntity build() {
            return new PlayerJpaEntity(this);
        }
    }
}
