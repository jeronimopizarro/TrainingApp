package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.RoutineJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutineJpaRepository extends JpaRepository<RoutineJpaEntity, Long> {}