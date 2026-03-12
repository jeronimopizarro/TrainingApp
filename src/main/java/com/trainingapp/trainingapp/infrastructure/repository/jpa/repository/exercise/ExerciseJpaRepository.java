package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.exercise;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.exercise.ExerciseJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExerciseJpaRepository extends JpaRepository<ExerciseJpaEntity, Long> {

    @Query("SELECT e FROM ExerciseJpaEntity e JOIN e.muscleGroups emg WHERE emg.muscleGroup.id = :muscleGroupId")
    List<ExerciseJpaEntity> findByMuscleGroupId(@Param("muscleGroupId") Long muscleGroupId);

    @Query("SELECT DISTINCT e FROM ExerciseJpaEntity e " +
            "LEFT JOIN e.muscleGroups emg " +
            "WHERE (e.isBase = true OR e.gymId = :gymId) " +
            "AND (:muscleGroupId IS NULL OR emg.muscleGroup.id = :muscleGroupId)")
    List<ExerciseJpaEntity> findAllowedForGym(
            @Param("gymId") Long gymId,
            @Param("muscleGroupId") Long muscleGroupId
    );
}