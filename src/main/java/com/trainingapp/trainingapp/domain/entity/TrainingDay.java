package com.trainingapp.trainingapp.domain.entity;


import com.trainingapp.trainingapp.web.dto.UpdateRoutineRequest.UpdateRoutineDetailRequest;
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

    public void addDetails(Long exerciseId, int sets, int repsMin, int repsMax, int targetRIR, Double suggestedWeight, String notes) {
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
        RoutineDetail detail = new RoutineDetail(exerciseId, exerciseOrder, sets, repsMin, repsMax, targetRIR,suggestedWeight, notes);
        this.details.add(detail);
    }


    public void updateName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null.");
        }
        this.name = newName;
    }

    public void syncDetails(List<UpdateRoutineDetailRequest> incomingDetails) {
        // 1. ELIMINAR (DELETE): Sacamos de nuestra lista los ejercicios que ya no vienen en el JSON
        List<Long> incomingIds = incomingDetails.stream()
                .map(UpdateRoutineDetailRequest::id)
                .filter(Objects::nonNull)
                .toList();

        // Esto dispara el Orphan Removal en la base de datos automáticamente
        this.details.removeIf(detail -> detail.getId() != null && !incomingIds.contains(detail.getId()));

        // 2. ACTUALIZAR O AGREGAR (UPDATE / INSERT)
        for (UpdateRoutineDetailRequest incomingDetail : incomingDetails) {
            if (incomingDetail.id() == null) {
                // Es un ejercicio nuevo, lo creamos y agregamos
                this.addDetails(
                        incomingDetail.exerciseId(),
                        incomingDetail.sets(), incomingDetail.repsMin(), incomingDetail.repsMax(),
                        incomingDetail.targetRIR(), incomingDetail.suggestedWeight(), incomingDetail.notes()
                );
            } else {
                // Es un ejercicio existente, lo buscamos y lo actualizamos
                this.details.stream()
                        .filter(detail -> incomingDetail.id().equals(detail.getId()))
                        .findFirst()
                        .ifPresent(existingDetail -> existingDetail.update(
                                incomingDetail.exerciseId(),
                                incomingDetail.sets(), incomingDetail.repsMin(), incomingDetail.repsMax(),
                                incomingDetail.targetRIR(), incomingDetail.suggestedWeight(), incomingDetail.notes()
                        ));
            }
        }
    }

}