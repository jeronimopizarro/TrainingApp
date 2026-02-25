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

    public RoutineDetail(Long exerciseId, int orderNumber, int sets, int repsMin, int repsMax, int targetRIR,
                         Double suggestedWeight, String notes) {
    this.exerciseId = exerciseId;
    this.orderNumber = orderNumber;
    this.sets = sets;
    this.repsMin = repsMin;
    this.repsMax = repsMax;
    this.targetRIR = targetRIR;
    this.suggestedWeight = suggestedWeight;
    this.notes = notes;
    }
}
