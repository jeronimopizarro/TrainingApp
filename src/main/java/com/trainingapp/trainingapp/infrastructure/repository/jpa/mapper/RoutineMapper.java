package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper;

import com.trainingapp.trainingapp.domain.entity.Routine;
import com.trainingapp.trainingapp.domain.entity.RoutineDetail;
import com.trainingapp.trainingapp.domain.entity.TrainingDay;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.RoutineDetailJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.RoutineJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.TrainingDayJpaEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RoutineMapper {

    public RoutineJpaEntity toEntity(Routine domain) {
        if (domain == null) return null;

        RoutineJpaEntity entity = new RoutineJpaEntity();

        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setStartDate(domain.getStartDate());
        entity.setEndDate(domain.getEndDate());
        entity.setMemberId(domain.getMemberId());
        entity.setTrainerId(domain.getTrainerId());
        entity.setStatus(domain.getStatus());

        if (domain.getDays() != null) {
            entity.setDays(
                    domain.getDays().stream()
                            .map(dayDomain -> mapDayToEntity(dayDomain, entity))
                            .collect(Collectors.toList())
            );
        }

        return entity;
    }

    // 2. MÉTODO DE AYUDA PARA EL DÍA
    private TrainingDayJpaEntity mapDayToEntity(TrainingDay dayDomain, RoutineJpaEntity parentRoutine) {
        TrainingDayJpaEntity dayEntity = new TrainingDayJpaEntity();
        dayEntity.setId(dayDomain.getId());
        dayEntity.setName(dayDomain.getName());
        dayEntity.setOrderNumber(dayDomain.getOrderNumber());

        dayEntity.setRoutine(parentRoutine);

        if (dayDomain.getDetails() != null) {
            dayEntity.setDetails(
                    dayDomain.getDetails().stream()
                            .map(detailDomain -> mapDetailToEntity(detailDomain, dayEntity))
                            .collect(Collectors.toList())
            );
        }

        return dayEntity;
    }

    // 3. MÉTODO DE AYUDA PARA EL EJERCICIO (Detalle)
    private RoutineDetailJpaEntity mapDetailToEntity(RoutineDetail detailDomain, TrainingDayJpaEntity parentDay) {
        RoutineDetailJpaEntity detailEntity = new RoutineDetailJpaEntity();
        detailEntity.setId(detailDomain.getId());
        detailEntity.setExerciseId(detailDomain.getExerciseId());
        detailEntity.setOrderNumber(detailDomain.getOrderNumber());
        detailEntity.setSets(detailDomain.getSets());
        detailEntity.setRepsMin(detailDomain.getRepsMin());
        detailEntity.setRepsMax(detailDomain.getRepsMax());
        detailEntity.setTargetRIR(detailDomain.getTargetRIR());
        detailEntity.setSuggestedWeight(detailDomain.getSuggestedWeight());
        detailEntity.setNotes(detailDomain.getNotes());

        detailEntity.setTrainingDay(parentDay);

        return detailEntity;
    }

    public Routine toDomain(RoutineJpaEntity entity) {
        if (entity == null) return null;

        Routine domain = new Routine(entity.getName(), entity.getMemberId(), entity.getTrainerId());

        domain.setId(entity.getId());
        domain.setStartDate(entity.getStartDate());
        domain.setEndDate(entity.getEndDate());
        domain.setStatus(entity.getStatus());

        if (entity.getDays() != null) {
            domain.setDays(
                    entity.getDays().stream()
                            .map(this::mapDayToDomain)
                            .collect(Collectors.toList())
            );
        }

        return domain;
    }

    // 2. MÉTODO DE AYUDA PARA EL DÍA
    private TrainingDay mapDayToDomain(TrainingDayJpaEntity dayEntity) {
        TrainingDay dayDomain = new TrainingDay(dayEntity.getName(), dayEntity.getOrderNumber());
        dayDomain.setId(dayEntity.getId());
        dayDomain.setOrderNumber(dayEntity.getOrderNumber());

        if (dayEntity.getDetails() != null) {
            dayDomain.setDetails(
                    dayEntity.getDetails().stream()
                            .map(this::mapDetailToDomain)
                            .collect(Collectors.toList())
            );
        }
        return dayDomain;
    }
    // 3. MÉTODO DE AYUDA PARA EL EJERCICIO (Detalle)
    private RoutineDetail mapDetailToDomain(RoutineDetailJpaEntity detailEntity) {

        RoutineDetail detailDomain = new RoutineDetail(
                detailEntity.getExerciseId(),
                detailEntity.getOrderNumber(),
                detailEntity.getSets(),
                detailEntity.getRepsMin(),
                detailEntity.getRepsMax(),
                detailEntity.getTargetRIR(),
                detailEntity.getSuggestedWeight(),
                detailEntity.getNotes()
        );

        detailDomain.setId(detailEntity.getId());

        return detailDomain;
    }
}
