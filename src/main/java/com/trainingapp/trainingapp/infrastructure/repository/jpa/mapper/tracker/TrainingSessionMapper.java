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
        if (entity == null) return null;

        // 1. Reconstruimos el historial de series de abajo hacia arriba
        List<SetLog> domainLogs = new ArrayList<>();
        if (entity.getSets() != null) {
            domainLogs = entity.getSets().stream()
                    .map(logEntity -> SetLog.restore(
                            logEntity.getId(),
                            logEntity.getExerciseId(),
                            logEntity.getSetNumber(),
                            logEntity.getRepsPerformed(),
                            logEntity.getWeightLifted(),
                            logEntity.getRir(),
                            logEntity.getNotes()
                    )).toList();
        }

        // 2. Reconstruimos la sesión completa con los datos restaurados
        return TrainingSession.restore(
                entity.getId(),
                entity.getMemberId(),
                entity.getRoutineId(), // Puede ser null si es entrenamiento libre
                entity.getGymId(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getStatus(),
                domainLogs
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