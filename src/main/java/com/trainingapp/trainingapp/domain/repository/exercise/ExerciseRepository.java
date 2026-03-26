package com.trainingapp.trainingapp.domain.repository.exercise;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository {
    Exercise save(Exercise exercise);

    Optional<Exercise> findById(Long id);

    List<Exercise> findAll();

    List<Exercise> findByMuscleGroupId(Long muscleGroupId);

    List<Exercise> findAllById(List<Long> ids);

    List<Exercise> findAllowedForGym(Long gymId, Long muscleGroupId);

    boolean existsByNameAndGymId(String name, Long gymId);

    boolean existsBaseExerciseByName(String name);

    boolean existsByNameAndGymIdAndIdNot(String name, Long gymId, Long id);

    boolean existsBaseExerciseByNameAndIdNot(String name, Long id);

    List<Exercise> findByGymId(Long gymId);
}