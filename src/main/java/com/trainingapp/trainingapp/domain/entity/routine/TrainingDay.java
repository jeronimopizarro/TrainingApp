package com.trainingapp.trainingapp.domain.entity.routine;


import com.trainingapp.trainingapp.web.dto.routine.UpdateRoutineRequest.UpdateRoutineDetailRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class TrainingDay {

    private Long id;
    private String name;
    private int orderNumber;

    private List<RoutineDetail> details;

    public TrainingDay(String name, int orderNumber) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null.");
        }
        if (orderNumber < 1) {
            throw new IllegalArgumentException("Order number cannot be less than 1.");
        }

        this.name = name;
        this.orderNumber = orderNumber;
        this.details = new ArrayList<>();
    }

    public void addDetails(Long exerciseId, int sets, int repsMin, int repsMax, int targetRIR,
                           Double suggestedWeight, String notes) {
        if (exerciseId == null || exerciseId <= 0) {
            throw new IllegalArgumentException("Exercise Id cannot be null.");
        }
        if (sets < 1) {
            throw new IllegalArgumentException("Sets cannot be less than 1.");
        }
        if (repsMin < 1) {
            throw new IllegalArgumentException("Reps min cannot be less than 1.");
        }
        if (repsMax < 1) {
            throw new IllegalArgumentException("Reps max cannot be less than 1.");
        }
        if (repsMax < repsMin) {
            throw new IllegalArgumentException("Reps max cannot be less than repsMin.");
        }
        if (targetRIR < 0) {
            throw new IllegalArgumentException("Target rir cannot be less than 0.");
        }
        if (suggestedWeight < 0) {
            throw new IllegalArgumentException("Suggest weight cannot be less than 0.");
        }

        int exerciseOrder = this.details.size() + 1;
        RoutineDetail detail = new RoutineDetail(exerciseId, exerciseOrder, sets, repsMin, repsMax,
                targetRIR, suggestedWeight, notes);
        this.details.add(detail);
    }


    public void updateName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null.");
        }
        this.name = newName;
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
}