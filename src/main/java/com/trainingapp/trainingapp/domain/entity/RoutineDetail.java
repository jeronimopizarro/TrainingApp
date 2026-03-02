package com.trainingapp.trainingapp.domain.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoutineDetail {

    private Long id;
    private int orderNumber;
    private int sets;
    private int repsMin;
    private int repsMax;
    private Double suggestedWeight;
    private int targetRIR;
    private String notes;

    private Long exerciseId;

    public RoutineDetail(Long exerciseId,
                         int orderNumber,
                         int sets,
                         int repsMin,
                         int repsMax,
                         int targetRIR,
                         Double suggestedWeight,
                         String notes) {
        this.exerciseId = exerciseId;
        this.orderNumber = orderNumber;
        this.sets = sets;
        this.repsMin = repsMin;
        this.repsMax = repsMax;
        this.targetRIR = targetRIR;
        this.suggestedWeight = suggestedWeight;
        this.notes = notes;
    }

    public void update(Long exerciseId,
                       int sets,
                       int repsMin,
                       int repsMax,
                       int targetRIR,
                       Double suggestedWeight,
                       String notes) {
        if (exerciseId == null || exerciseId <= 0)
            throw new IllegalArgumentException("Exercise Id cannot be null.");
        if (sets < 1) throw new IllegalArgumentException("Sets cannot be less than 1.");
        if (repsMin < 1) throw new IllegalArgumentException("Reps min cannot be less than 1.");
        if (repsMax < 1) throw new IllegalArgumentException("Reps max cannot be less than 1.");
        if (repsMax < repsMin)
            throw new IllegalArgumentException("Reps max cannot be less than repsMin.");
        if (targetRIR < 0) throw new IllegalArgumentException("Target rir cannot be less than 0.");
        if (suggestedWeight < 0)
            throw new IllegalArgumentException("Suggest weight cannot be less than 0.");

        this.exerciseId = exerciseId;
        this.sets = sets;
        this.repsMin = repsMin;
        this.repsMax = repsMax;
        this.targetRIR = targetRIR;
        this.suggestedWeight = suggestedWeight;
        this.notes = notes;
    }
}