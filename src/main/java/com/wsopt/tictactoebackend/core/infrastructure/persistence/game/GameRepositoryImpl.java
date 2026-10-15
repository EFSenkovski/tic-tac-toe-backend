package com.wsopt.tictactoebackend.core.infrastructure.persistence.game;

import com.wsopt.tictactoebackend.core.app.port.out.GameRepository;
import com.wsopt.tictactoebackend.core.domain.game.Game;
import com.wsopt.tictactoebackend.core.domain.game.player.Player;
import com.wsopt.tictactoebackend.core.infrastructure.persistence.player.PlayerJpaEntity;
import com.wsopt.tictactoebackend.core.infrastructure.persistence.player.PlayerMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public class GameRepositoryImpl implements GameRepository {

    private final GameJpaRepository repository;
    private final GameMapper gameMapper;
    private final PlayerMapper playerMapper;

    public GameRepositoryImpl(GameJpaRepository repository, GameMapper gameMapper, PlayerMapper playerMapper) {
        this.repository = repository;
        this.gameMapper = gameMapper;
        this.playerMapper = playerMapper;
    }

    @Override
    @Transactional
    public Game saveGame(Game game) {
        var entity = this.repository.findByExternalId(game.getExternalId())
                .map(existing -> updateExisting(existing, game))
                .orElseGet(() -> gameMapper.toGameJpaEntity(game));
        var saved = this.repository.save(entity);
        return gameMapper.toGame(saved);
    }

    @Override
    public Optional<Game> findByExternalId(UUID externalId) {
        return this.repository.findByExternalId(externalId)
                .map(gameMapper::toGame);
    }

    @Override
    public Optional<Game> findByExternalIdForUpdate(UUID externalId) {
        return this.repository.findLockedByExternalId(externalId)
                .map(gameMapper::toGame);
    }

    private GameJpaEntity updateExisting(GameJpaEntity existing, Game game) {
        var playerOne = persistedOrNew(existing.getPlayerOne(), game.getPlayerOne());
        var playerTwo = persistedOrNew(existing.getPlayerTwo(), game.getPlayerTwo());

        existing.setPlayerOne(playerOne);
        existing.setPlayerTwo(playerTwo);
        existing.setCurrentPlayer(game.getCurrentPlayer() == game.getPlayerOne() ? playerOne : playerTwo);
        gameMapper.copyStateTo(game, existing);
        return existing;
    }

    private PlayerJpaEntity persistedOrNew(PlayerJpaEntity persisted, Player player) {
        if (persisted == null) {
            return playerMapper.toPlayerJpaEntity(player);
        }
        playerMapper.copyStateTo(player, persisted);
        return persisted;
    }
}
