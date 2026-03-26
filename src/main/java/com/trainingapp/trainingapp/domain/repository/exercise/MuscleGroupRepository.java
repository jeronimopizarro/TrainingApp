package com.trainingapp.trainingapp.domain.repository.exercise;

import com.trainingapp.trainingapp.domain.entity.exercise.MuscleGroup;

import java.util.List;
import java.util.Optional;

public interface MuscleGroupRepository {
    List<MuscleGroup> findAll();

    Optional<MuscleGroup> findById(Long id);
}