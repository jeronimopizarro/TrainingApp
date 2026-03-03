package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.exercise;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.exercise.MuscleGroupJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MuscleGroupJpaRepository extends JpaRepository<MuscleGroupJpaEntity, Long> {
}