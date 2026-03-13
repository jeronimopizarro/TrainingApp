package com.trainingapp.trainingapp.infrastructure.repository.jpa.impl.user;

import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.repository.user.UserRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.user.UserMapper;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.user.UserJpaRepository;
import org.springframework.stereotype.Repository;

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
}
