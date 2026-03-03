package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.exercise;

import com.trainingapp.trainingapp.domain.entity.exercise.MuscleGroup;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.exercise.MuscleGroupJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class MuscleGroupMapper {
    public MuscleGroup toDomain(MuscleGroupJpaEntity entity){
        if(entity == null) return null;

        MuscleGroup domain = new MuscleGroup(entity.getName(), entity.getDescription());
        domain.setId(entity.getId());

        return domain;
    }
}