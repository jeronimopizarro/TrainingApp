package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.routine;

import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.routine.RoutineRequest;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.routine.RoutineRequestJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RoutineRequestMapper {

    public RoutineRequest toDomain(RoutineRequestJpaEntity entity) {
        if (entity == null) return null;

        return RoutineRequest.restore(
                entity.getId(),
                entity.getMemberId(),
                entity.getGymId(),
                entity.getRequestDate(),
                entity.getStatus(),
                entity.getAssignedTrainerId(),
                entity.getRoutineId(),
                entity.getTargetTrainerId(),
                entity.getAvailableDays(),
                entity.getExperienceLevel(),
                entity.getInjuries(),
                entity.getPrimaryGoal()
        );
    }

    public RoutineRequestJpaEntity toEntity(RoutineRequest domain) {
        if (domain == null) return null;

        RoutineRequestJpaEntity entity = new RoutineRequestJpaEntity();
        entity.setId(domain.getId());
        entity.setMemberId(domain.getMemberId());
        entity.setGymId(domain.getGymId());
        entity.setRequestDate(domain.getRequestDate());
        entity.setStatus(domain.getStatus());

        return entity;
    }
}