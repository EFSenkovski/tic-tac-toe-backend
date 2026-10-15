package com.wsopt.tictactoebackend.core.infrastructure.persistence.player;

import com.wsopt.tictactoebackend.core.app.port.out.PlayerRepository;
import com.wsopt.tictactoebackend.core.domain.game.player.Player;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PlayerRepositoryImpl implements PlayerRepository {

    private final PlayerJpaRepository repository;
    private final PlayerMapper playerMapper;

    public PlayerRepositoryImpl(PlayerJpaRepository repository, PlayerMapper playerMapper) {
        this.repository = repository;
        this.playerMapper = playerMapper;
    }

    @Override
    public Optional<Player> findByExternalId(UUID playerId) {
        return this.repository.findByExternalId(playerId)
                .map(playerMapper::toPlayer);
    }
}
