package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.exercise;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.exercise.ExerciseJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.exercise.ExerciseMuscleGroupJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.exercise.MuscleGroupJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ExerciseMapper {

    public Exercise toDomain(ExerciseJpaEntity entity) {
        if (entity == null) return null;

        Exercise domain = new Exercise(entity.getName(), entity.getDescription(),
                entity.getImageUrl(), entity.getVideoUrl(), entity.getIsBase(),
                entity.getCreatedByUserId(), entity.getGymId());

        domain.setId(entity.getId());

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
}