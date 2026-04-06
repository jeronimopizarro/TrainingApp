package com.trainingapp.trainingapp.domain.repository.user;

import com.trainingapp.trainingapp.domain.entity.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    boolean existsByEmail(String email);

    List<User> findUsersWithoutAccessSince(LocalDateTime threshold);

    void save(User user);
}