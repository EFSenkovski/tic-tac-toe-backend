package com.wsopt.tictactoebackend.core.infrastructure.persistence.board;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "boards")
public class BoardJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "boards_seq")
    @SequenceGenerator(name = "boards_seq", sequenceName = "boards_seq", allocationSize = 1)
    private Long id;

    @NotNull
    private UUID externalId;

    @NotNull
    private String state;

    public BoardJpaEntity() {
    }

    public BoardJpaEntity(String state, UUID externalId) {
        this.state = state;
        this.externalId = externalId;
    }

    public UUID getExternalId() {
        return externalId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
