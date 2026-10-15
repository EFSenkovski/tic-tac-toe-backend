package com.wsopt.tictactoebackend.core.domain.user;

import java.util.UUID;

public class User {
    private final UUID externalId;
    private final String email;

    public User(UUID externalId, String email) {
        this.externalId = externalId;
        this.email = email;
    }

    public User(String email) {
        this(UUID.randomUUID(), email);
    }

    public UUID getExternalId() {
        return externalId;
    }

    public String getEmail() {
        return email;
    }
}
