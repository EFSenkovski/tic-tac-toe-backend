package com.wsopt.tictactoebackend.core.domain.game.player;

import com.wsopt.tictactoebackend.core.domain.game.Figure;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class PlayerFactoryTest {

    @Test
    void given_AiType_WhenRebuildingPlayer_ThenShouldBeAiPlayerWithSameExternalId() {
        var externalId = UUID.randomUUID();

        var player = PlayerFactory.rebuildPlayer(Figure.O, externalId, PlayerType.HARD, null, Rematch.NOT_ASKED);

        assertThat(player).isInstanceOf(AiPlayer.class);
        assertThat(player.getExternalId()).isEqualTo(externalId);
        assertThat(player.getPlayerType()).isEqualTo(PlayerType.HARD);
        assertThat(player.getFigure()).isEqualTo(Figure.O);
    }

    @Test
    void given_UserType_WhenRebuildingPlayer_ThenShouldBePlainPlayerWithUser() {
        var externalId = UUID.randomUUID();
        var userId = UUID.randomUUID();

        var player = PlayerFactory.rebuildPlayer(Figure.X, externalId, PlayerType.USER, userId, Rematch.ASKED);

        assertThat(player).isExactlyInstanceOf(Player.class);
        assertThat(player.getExternalId()).isEqualTo(externalId);
        assertThat(player.getUserExternalId()).isEqualTo(userId);
        assertThat(player.getRematch()).isEqualTo(Rematch.ASKED);
    }
}
