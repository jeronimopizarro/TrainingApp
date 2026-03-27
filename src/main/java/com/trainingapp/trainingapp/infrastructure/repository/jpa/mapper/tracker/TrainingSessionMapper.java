package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.tracker;

import com.trainingapp.trainingapp.domain.entity.tracker.SetLog;
import com.trainingapp.trainingapp.domain.entity.tracker.TrainingSession;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.tracker.SetLogJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.tracker.TrainingSessionJpaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrainingSessionMapper {

    public TrainingSession toDomain(TrainingSessionJpaEntity entity) {
        List<SetLog> sets = mapSetsToDomain(entity.getSets());

        return new TrainingSession(
                entity.getId(),
                entity.getMemberId(),
                entity.getRoutineId(),
                entity.getGymId(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getStatus(),
                sets
        );
    }

    private List<SetLog> mapSetsToDomain(List<SetLogJpaEntity> setEntities) {
        if (setEntities == null) return new ArrayList<>();
        return setEntities.stream()
                .map(this::toSetDomain)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private SetLog toSetDomain(SetLogJpaEntity setEntity) {
        return new SetLog(
                setEntity.getId(),
                setEntity.getExerciseId(),
                setEntity.getSetNumber(),
                setEntity.getRepsPerformed(),
                setEntity.getWeightLifted(),
                setEntity.getRir(),
                setEntity.getNotes()
        );
    }

    public TrainingSessionJpaEntity toEntity(TrainingSession domain) {
        TrainingSessionJpaEntity entity = new TrainingSessionJpaEntity();
        entity.setId(domain.getId());
        entity.setMemberId(domain.getMemberId());
        entity.setRoutineId(domain.getRoutineId());
        entity.setGymId(domain.getGymId());
        entity.setStartTime(domain.getStartTime());
        entity.setEndTime(domain.getEndTime());
        entity.setStatus(domain.getStatus());

        domain.getSets().forEach(setLog -> entity.addSetLog(toSetEntity(setLog)));

        return entity;
    }

    private SetLogJpaEntity toSetEntity(SetLog setLog) {
        SetLogJpaEntity entity = new SetLogJpaEntity();
        entity.setId(setLog.getId());
        entity.setExerciseId(setLog.getExerciseId());
        entity.setSetNumber(setLog.getSetNumber());
        entity.setRepsPerformed(setLog.getRepsPerformed());
        entity.setWeightLifted(setLog.getWeightLifted());
        entity.setRir(setLog.getRir());
        entity.setNotes(setLog.getNotes());
        return entity;
    }
}