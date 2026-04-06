package com.trainingapp.trainingapp.domain.entity.routine;

import com.trainingapp.trainingapp.domain.exception.routine.InvalidRoutineMetricsException;
import lombok.Getter;

@Getter
public class RoutineDetail {

    private final Long id;
    private int orderNumber;
    private int sets;
    private int repsMin;
    private int repsMax;
    private Double suggestedWeight;
    private int targetRIR;
    private String notes;
    private Long exerciseId;

    private RoutineDetail(Long id, Long exerciseId, int orderNumber, int sets, int repsMin,
                          int repsMax, int targetRIR, Double suggestedWeight, String notes) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.orderNumber = orderNumber;
        this.sets = sets;
        this.repsMin = repsMin;
        this.repsMax = repsMax;
        this.targetRIR = targetRIR;
        this.suggestedWeight = suggestedWeight;
        this.notes = notes;
        validate();
    }

    private void validate() {
        if (this.exerciseId == null || this.exerciseId <= 0)
            throw new InvalidRoutineMetricsException("El ID del ejercicio es inválido.");
        if (this.sets < 1)
            throw new InvalidRoutineMetricsException("Las series deben ser al menos 1.");
        if (this.repsMin < 1) throw new InvalidRoutineMetricsException(
                "Las repeticiones mínimas deben ser al menos 1.");
        if (this.repsMax < 1) throw new InvalidRoutineMetricsException(
                "Las repeticiones máximas deben ser al menos 1.");
        if (this.repsMax < this.repsMin) throw new InvalidRoutineMetricsException(
                "El máximo de repeticiones no puede ser menor al mínimo.");
        if (this.targetRIR < 0)
            throw new InvalidRoutineMetricsException("El RIR no puede ser negativo.");
        if (this.suggestedWeight < 0)
            throw new InvalidRoutineMetricsException("El peso sugerido no puede ser negativo.");
    }

    public static RoutineDetail createNew(Long exerciseId, int orderNumber, int sets, int repsMin,
                                          int repsMax, int targetRIR, Double suggestedWeight,
                                          String notes) {
        return new RoutineDetail(null, exerciseId, orderNumber, sets, repsMin, repsMax, targetRIR,
                suggestedWeight, notes);
    }

    public static RoutineDetail restore(Long id, Long exerciseId, int orderNumber, int sets,
                                        int repsMin,
                                        int repsMax, int targetRIR, Double suggestedWeight,
                                        String notes) {
        return new RoutineDetail(id, exerciseId, orderNumber, sets, repsMin, repsMax, targetRIR,
                suggestedWeight, notes);
    }

    public void update(Long exerciseId, int sets, int repsMin, int repsMax, int targetRIR,
                       Double suggestedWeight, String notes) {
        this.exerciseId = exerciseId;
        this.sets = sets;
        this.repsMin = repsMin;
        this.repsMax = repsMax;
        this.targetRIR = targetRIR;
        this.suggestedWeight = suggestedWeight;
        this.notes = notes;
        validate();
    }
}