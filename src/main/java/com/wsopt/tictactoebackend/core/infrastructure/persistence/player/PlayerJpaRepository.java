package com.wsopt.tictactoebackend.core.infrastructure.persistence.player;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlayerJpaRepository extends JpaRepository<PlayerJpaEntity, Long> {
    Optional<PlayerJpaEntity> findByExternalId(UUID externalId);
}
