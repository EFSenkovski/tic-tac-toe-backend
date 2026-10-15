package com.wsopt.tictactoebackend.core.infrastructure.persistence.board;

import com.wsopt.tictactoebackend.core.domain.game.Board;
import org.springframework.stereotype.Component;

@Component
public class BoardMapper {
    public BoardJpaEntity toBoardJpaEntity(Board board) {
        return new BoardJpaEntity(
                board.calculateState(),
                board.getExternalId()
        );
    }

    public Board toBoard(BoardJpaEntity jpaEntity) {
        return new Board(
                jpaEntity.getState(),
                jpaEntity.getExternalId()
        );
    }
}
