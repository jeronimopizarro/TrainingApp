package com.trainingapp.trainingapp.domain.entity.tracker;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class SetLog {

    private Long id;
    private Long exerciseId;
    private Integer setNumber; // Ej: Serie 1, Serie 2
    private Integer repsPerformed;
    private BigDecimal weightLifted; // En Kg
    private String notes;

    public SetLog(Long id, Long exerciseId, Integer setNumber, Integer repsPerformed,
                  BigDecimal weightLifted, String notes) {
        validateSet(exerciseId, setNumber, repsPerformed, weightLifted);
        this.id = id;
        this.exerciseId = exerciseId;
        this.setNumber = setNumber;
        this.repsPerformed = repsPerformed;
        this.weightLifted = weightLifted;
        this.notes = notes;
    }

    public static SetLog recordNew(Long exerciseId, Integer setNumber, Integer repsPerformed,
                                   BigDecimal weightLifted, String notes) {
        return new SetLog(null, exerciseId, setNumber, repsPerformed, weightLifted, notes);
    }

    private void validateSet(Long exerciseId, Integer setNumber, Integer repsPerformed,
                             BigDecimal weightLifted) {
        if (exerciseId == null) throw new IllegalArgumentException("El ejercicio es obligatorio.");
        if (setNumber == null || setNumber <= 0)
            throw new IllegalArgumentException("El número de serie debe ser mayor a cero.");
        if (repsPerformed == null || repsPerformed < 0)
            throw new IllegalArgumentException("Las repeticiones no pueden ser negativas.");
        if (weightLifted == null || weightLifted.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("El peso no puede ser negativo.");
    }
}