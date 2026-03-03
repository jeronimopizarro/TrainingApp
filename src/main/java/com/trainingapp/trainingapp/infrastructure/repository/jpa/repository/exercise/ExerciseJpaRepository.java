package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.exercise;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.exercise.ExerciseJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ExerciseJpaRepository extends JpaRepository<ExerciseJpaEntity,Long> {

    @Query("SELECT e FROM ExerciseJpaEntity e JOIN e.muscleGroups emg WHERE emg.muscleGroup.id = :muscleGroupId")
    List<ExerciseJpaEntity> findByMuscleGroupId(@Param("muscleGroupId") Long muscleGroupId);
}