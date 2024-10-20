package com.ing.user.port;

import com.ing.user.model.User;

import java.util.Optional;

public interface UserPort {

    Optional<User> findById(String id);

    Optional<User> findByUsername(String username);

    User save(User user);

}