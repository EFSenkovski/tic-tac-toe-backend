package com.wsopt.tictactoebackend.core.infrastructure.persistence.user;

import com.wsopt.tictactoebackend.core.domain.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserJpaEntity toUserJpaEntity(User user) {
        return new UserJpaEntity(
                user.getExternalId(),
                user.getEmail()
        );
    }

    public User toUser(UserJpaEntity jpaEntity) {
        return new User(
                jpaEntity.getExternalId(),
                jpaEntity.getEmail()
        );
    }
}
