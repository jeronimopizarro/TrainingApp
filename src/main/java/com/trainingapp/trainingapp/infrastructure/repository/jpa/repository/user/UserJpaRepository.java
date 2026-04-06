package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.user;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByEmailAndActiveTrue(String email);

    Optional<UserJpaEntity> findByIdAndActiveTrue(Long id);

    boolean existsByEmailAndActiveTrue(String email);

    // Buscamos usuarios activos, que fueron creados hace más del tiempo límite,
    // y que NO tienen ningún registro de acceso posterior a esa fecha.
    @Query("SELECT u FROM UserJpaEntity u WHERE u.isActive = true " +
            "AND u.createdAt < :threshold " +
            "AND NOT EXISTS (SELECT a FROM AccessLogJpaEntity a WHERE a.memberId = u.id AND a.timestamp >= :threshold)")
    List<UserJpaEntity> findInactiveUsersByAccessLog(@Param("threshold") LocalDateTime threshold);
}