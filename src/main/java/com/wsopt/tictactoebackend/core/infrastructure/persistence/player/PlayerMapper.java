package com.wsopt.tictactoebackend.core.infrastructure.persistence.player;

import com.wsopt.tictactoebackend.core.domain.game.player.Player;
import com.wsopt.tictactoebackend.core.domain.game.player.PlayerFactory;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapper {

    public PlayerJpaEntity toPlayerJpaEntity(Player player) {
        if (player == null) return null;
        return PlayerJpaEntity.newPlayerJpaEntity()
                .externalId(player.getExternalId())
                .figure(player.getFigure())
                .type(player.getPlayerType())
                .rematch(player.getRematch())
                .build();
    }

    public void copyStateTo(Player player, PlayerJpaEntity jpaEntity) {
        jpaEntity.setRematch(player.getRematch());
    }

    public Player toPlayer(PlayerJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;
        var user = jpaEntity.getUser();
        return PlayerFactory.rebuildPlayer(
                jpaEntity.getFigure(),
                jpaEntity.getExternalId(),
                jpaEntity.getType(),
                user == null ? null : user.getExternalId(),
                jpaEntity.getRematch()
        );
    }
}
