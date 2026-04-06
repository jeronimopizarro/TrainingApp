package com.trainingapp.trainingapp.infrastructure.repository.jpa.impl.user;

import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.repository.user.UserRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.UserJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.user.UserMapper;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.user.UserJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    public UserRepositoryImpl(UserJpaRepository userJpaRepository, UserMapper userMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmailAndActiveTrue(email)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findByIdAndActiveTrue(id)
                .map(userMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmailAndActiveTrue(email);
    }

    @Override
    public List<User> findUsersWithoutAccessSince(LocalDateTime threshold) {
        return userJpaRepository.findInactiveUsersByAccessLog(threshold).stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public void save(User user) {
        UserJpaEntity entity = userMapper.toEntity(user);
        userJpaRepository.save(entity);
    }
}