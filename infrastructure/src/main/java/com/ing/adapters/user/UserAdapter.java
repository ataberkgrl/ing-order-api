package com.ing.adapters.user;

import com.ing.adapters.user.entity.UserEntity;
import com.ing.adapters.user.jpa.UserJpaRepository;
import com.ing.user.model.User;
import com.ing.user.port.UserPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserAdapter implements UserPort {

    private final UserJpaRepository userJpaRepository;

    public UserAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<User> findById(String id) {
        return userJpaRepository.findById(id).map(this::toUser);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username).map(this::toUser);
    }

    @Override
    public User save(User user) {
        UserEntity entity = toUserEntity(user);
        UserEntity savedEntity = userJpaRepository.save(entity);
        return toUser(savedEntity);
    }

    private User toUser(UserEntity entity) {
        return new User(entity.getId(), entity.getUsername(), entity.getPassword(), entity.getUserType());
    }

    private UserEntity toUserEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setUserType(user.getUserType());
        return entity;
    }
}