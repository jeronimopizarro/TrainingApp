package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.exercise;

import com.trainingapp.trainingapp.domain.entity.exercise.MuscleGroup;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.exercise.MuscleGroupJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class MuscleGroupMapper {

    public MuscleGroup toDomain(MuscleGroupJpaEntity entity) {
        if (entity == null) return null;

        return MuscleGroup.restore(
                entity.getId(),
                entity.getName(),
                entity.getDescription());
    }
}