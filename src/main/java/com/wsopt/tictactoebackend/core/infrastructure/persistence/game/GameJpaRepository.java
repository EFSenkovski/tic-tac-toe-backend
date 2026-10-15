package com.wsopt.tictactoebackend.core.infrastructure.persistence.game;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

public interface GameJpaRepository extends JpaRepository<GameJpaEntity, Long> {
    Optional<GameJpaEntity> findByExternalId(UUID externalId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<GameJpaEntity> findLockedByExternalId(UUID externalId);
}
