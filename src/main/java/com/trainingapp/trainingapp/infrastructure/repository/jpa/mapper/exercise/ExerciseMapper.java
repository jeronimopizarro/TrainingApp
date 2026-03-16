package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.exercise;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.exercise.ExerciseJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.exercise.ExerciseMuscleGroupJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.exercise.MuscleGroupJpaEntity;
import com.trainingapp.trainingapp.web.dto.exercise.CreateExerciseRequest;
import com.trainingapp.trainingapp.web.dto.exercise.ExerciseResponse;
import org.springframework.stereotype.Component;

@Component
public class ExerciseMapper {

    public Exercise toDomain(ExerciseJpaEntity entity) {
        if (entity == null) return null;

        Exercise domain = new Exercise(entity.getName(), entity.getDescription(),
                entity.getImageUrl(), entity.getVideoUrl(), entity.getIsBase(),
                entity.getCreatedByUserId(), entity.getGymId());

        domain.setId(entity.getId());
        domain.setActive(entity.isActive());

        if (entity.getMuscleGroups() != null) {
            entity.getMuscleGroups().forEach(
                    mgEntity -> domain.addMuscleGroup(mgEntity.getMuscleGroup().getId(),
                            mgEntity.isPrimary()));
        }
        return domain;
    }

    public ExerciseJpaEntity toEntity(Exercise domain) {
        if (domain == null) return null;

        ExerciseJpaEntity entity = new ExerciseJpaEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setImageUrl(domain.getImageUrl());
        entity.setVideoUrl(domain.getVideoUrl());
        entity.setIsBase(domain.getIsBase());
        entity.setCreatedByUserId(domain.getCreatedByUserId());
        entity.setGymId(domain.getGymId());
        entity.setActive(domain.isActive());

        if (domain.getMuscleGroups() != null) {
            domain.getMuscleGroups().forEach(mgDomain -> {
                ExerciseMuscleGroupJpaEntity mgEntity = new ExerciseMuscleGroupJpaEntity();

                // Creamos un auxiliar del músculo solo con el ID para la relación
                MuscleGroupJpaEntity dummyMuscle = new MuscleGroupJpaEntity();
                dummyMuscle.setId(mgDomain.getMuscleGroupId());

                mgEntity.setExercise(entity);
                mgEntity.setMuscleGroup(dummyMuscle);
                mgEntity.setPrimary(mgDomain.isPrimary());

                entity.getMuscleGroups().add(mgEntity);
            });
        }

        return entity;
    }

    public Exercise toDomain(CreateExerciseRequest request, boolean isBase, Long gymId, Long userId) {
        if (request == null) return null;

        Exercise exercise = new Exercise(
                request.name(),
                request.description(),
                request.imageUrl(),
                request.videoUrl(),
                isBase,
                userId,
                gymId
        );

        if (request.muscleGroups() != null) {
            request.muscleGroups().forEach(mgRequest ->
                    exercise.addMuscleGroup(mgRequest.muscleGroupId(), mgRequest.isPrimary())
            );
        }

        return exercise;
    }

    public ExerciseResponse toResponse(Exercise exercise) {
        if (exercise == null) return null;

        return new ExerciseResponse(exercise.getId(), "Exercise created successfully");
    }
}