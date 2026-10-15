package com.wsopt.tictactoebackend.core.infrastructure.persistence.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @SequenceGenerator(name = "users_seq", sequenceName = "users_seq", allocationSize = 1)
    private Long id;

    @NotNull
    private UUID externalId;

    private String email;

    public UserJpaEntity() {
    }

    public UserJpaEntity(UUID externalId, String email) {
        this.externalId = externalId;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public UUID getExternalId() {
        return externalId;
    }

    public String getEmail() {
        return email;
    }
}
