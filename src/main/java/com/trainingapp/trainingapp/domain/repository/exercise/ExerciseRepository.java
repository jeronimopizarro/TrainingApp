package com.trainingapp.trainingapp.domain.repository.exercise;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository {
    Exercise save(Exercise exercise);
    Optional<Exercise> findById(Long id);
    List<Exercise> findAll();
    List<Exercise> findByMuscleGroupId(Long muscleGroupId);
    void delete(Exercise exercise);
    List<Exercise> findAllById(List<Long> ids);
    List<Exercise> findAllowedForGym(Long gymId, Long muscleGroupId);
}
