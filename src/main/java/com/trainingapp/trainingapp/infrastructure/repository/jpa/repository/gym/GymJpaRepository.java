package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.gym;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.gym.GymJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GymJpaRepository extends JpaRepository<GymJpaEntity, Long> {

    List<GymJpaEntity> findAllByActiveTrue();

    Optional<GymJpaEntity> findByIdAndActiveTrue(Long id);

    boolean existsByNameAndActiveTrue(String name);

    boolean existsByNameAndIdNotAndActiveTrue(String name, Long id);

    boolean existsByIdAndActiveTrue(Long id);
}