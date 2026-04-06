package com.trainingapp.trainingapp.domain.entity.routine;

import com.trainingapp.trainingapp.domain.exception.routine.InvalidRoutineMetricsException;
import com.trainingapp.trainingapp.domain.exception.routine.TrainingDayNameRequiredException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class TrainingDay {

    private final Long id;
    private String name;
    private int orderNumber;
    private List<RoutineDetail> details;

    private TrainingDay(Long id, String name, int orderNumber, List<RoutineDetail> details) {
        this.id = id;
        this.name = name;
        this.orderNumber = orderNumber;
        this.details = details != null ? new ArrayList<>(details) : new ArrayList<>();
        validate();
    }

    private void validate() {
        if (this.name == null || this.name.isBlank()) throw new TrainingDayNameRequiredException();
        if (this.orderNumber < 1) throw new InvalidRoutineMetricsException(
                "El número de orden del día no puede ser menor a 1.");
    }

    public static TrainingDay createNew(String name, int orderNumber) {
        return new TrainingDay(null, name, orderNumber, new ArrayList<>());
    }

    public static TrainingDay restore(Long id, String name, int orderNumber,
                                      List<RoutineDetail> details) {
        return new TrainingDay(id, name, orderNumber, details);
    }

    public void updateName(String newName) {
        this.name = newName;
        validate();
    }

    public void addDetails(Long exerciseId, int sets, int repsMin, int repsMax, int targetRIR,
                           Double suggestedWeight, String notes) {
        int exerciseOrder = this.details.size() + 1;

        RoutineDetail detail =
                RoutineDetail.createNew(exerciseId, exerciseOrder, sets, repsMin, repsMax,
                        targetRIR, suggestedWeight, notes);
        this.details.add(detail);
    }

    public void syncDetails(List<RoutineDetail> incomingDetails) {
        List<Long> incomingIds = incomingDetails.stream()
                .map(RoutineDetail::getId)
                .filter(Objects::nonNull)
                .toList();

        this.details.removeIf(
                detail -> detail.getId() != null && !incomingIds.contains(detail.getId()));

        for (RoutineDetail incomingDetail : incomingDetails) {
            if (incomingDetail.getId() == null) {
                this.addDetails(
                        incomingDetail.getExerciseId(),
                        incomingDetail.getSets(), incomingDetail.getRepsMin(),
                        incomingDetail.getRepsMax(),
                        incomingDetail.getTargetRIR(), incomingDetail.getSuggestedWeight(),
                        incomingDetail.getNotes()
                );
            } else {
                this.details.stream()
                        .filter(detail -> incomingDetail.getId().equals(detail.getId()))
                        .findFirst()
                        .ifPresent(existingDetail -> existingDetail.update(
                                incomingDetail.getExerciseId(),
                                incomingDetail.getSets(), incomingDetail.getRepsMin(),
                                incomingDetail.getRepsMax(),
                                incomingDetail.getTargetRIR(), incomingDetail.getSuggestedWeight(),
                                incomingDetail.getNotes()
                        ));
            }
        }
    }

    public void copyDetailsFrom(TrainingDay sourceDay) {
        for (RoutineDetail sourceDetail : sourceDay.getDetails()) {
            this.addDetails(
                    sourceDetail.getExerciseId(),
                    sourceDetail.getSets(),
                    sourceDetail.getRepsMin(),
                    sourceDetail.getRepsMax(),
                    sourceDetail.getTargetRIR(),
                    sourceDetail.getSuggestedWeight(),
                    sourceDetail.getNotes()
            );
        }
    }
}