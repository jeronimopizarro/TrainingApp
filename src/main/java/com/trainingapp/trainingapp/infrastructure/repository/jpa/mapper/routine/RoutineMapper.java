package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.routine;

import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.routine.RoutineDetail;
import com.trainingapp.trainingapp.domain.entity.routine.TrainingDay;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.routine.RoutineDetailJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.routine.RoutineJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.routine.TrainingDayJpaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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
        entity.setCreatedByUserId(domain.getCreatedByUserId());
        entity.setGymId(domain.getGymId());
        entity.setStatus(domain.getStatus());
        entity.setActive(domain.isActive());
        entity.setBase(domain.isBase());

        if (domain.getDays() != null) {
            entity.setDays(
                    domain.getDays().stream()
                            .map(dayDomain -> mapDayToEntity(dayDomain, entity))
                            .collect(Collectors.toList())
            );
        }

        return entity;
    }

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

        // Reconstruimos los Días y sus Detalles
        List<TrainingDay> domainDays = new ArrayList<>();

        if (entity.getDays() != null) {
            for (TrainingDayJpaEntity dayEntity : entity.getDays()) {

                // Reconstruimos los Detalles de este Día
                List<RoutineDetail> domainDetails = new ArrayList<>();
                if (dayEntity.getDetails() != null) {
                    for (RoutineDetailJpaEntity detailEntity : dayEntity.getDetails()) {
                        domainDetails.add(RoutineDetail.restore(
                                detailEntity.getId(),
                                detailEntity.getExerciseId(),
                                detailEntity.getOrderNumber(),
                                detailEntity.getSets(),
                                detailEntity.getRepsMin(),
                                detailEntity.getRepsMax(),
                                detailEntity.getTargetRIR(),
                                detailEntity.getSuggestedWeight(),
                                detailEntity.getNotes()
                        ));
                    }
                }

                // Reconstruimos el Día pasándole sus detalles
                domainDays.add(TrainingDay.restore(
                        dayEntity.getId(),
                        dayEntity.getName(),
                        dayEntity.getOrderNumber(),
                        domainDetails
                ));
            }
        }

        // Reconstruimos la Rutina Raíz pasándole los días ya armados
        return Routine.restore(
                entity.getId(),
                entity.getName(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getMemberId(),
                entity.getTrainerId(),
                entity.getCreatedByUserId(),
                entity.getGymId(),
                entity.getStatus(),
                entity.isActive(),
                entity.isBase(),
                domainDays
        );
    }
}