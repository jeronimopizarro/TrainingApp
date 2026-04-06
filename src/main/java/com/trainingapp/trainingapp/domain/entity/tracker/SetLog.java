package com.trainingapp.trainingapp.domain.entity.tracker;

import com.trainingapp.trainingapp.domain.exception.tracker.InvalidSetMetricsException;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class SetLog {

    private final Long id;
    private Long exerciseId;
    private Integer setNumber; // Ej: Serie 1, Serie 2
    private Integer repsPerformed;
    private BigDecimal weightLifted; // En Kg
    private Integer rir;
    private String notes;

    public SetLog(Long id, Long exerciseId, Integer setNumber, Integer repsPerformed,
                  BigDecimal weightLifted, Integer rir, String notes) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.setNumber = setNumber;
        this.repsPerformed = repsPerformed;
        this.weightLifted = weightLifted;
        this.rir = rir;
        this.notes = notes;
        validate();
    }

    private void validate() {
        if (this.exerciseId == null) throw new InvalidSetMetricsException("El ID del ejercicio es obligatorio.");
        if (this.setNumber == null || this.setNumber <= 0) throw new InvalidSetMetricsException("El número de serie debe ser mayor a cero.");
        if (this.repsPerformed == null || this.repsPerformed <= 0) throw new InvalidSetMetricsException("Las repeticiones deben ser mayores a cero.");
        if (this.weightLifted != null && this.weightLifted.compareTo(BigDecimal.ZERO) < 0) throw new InvalidSetMetricsException("El peso no puede ser negativo.");
        if (this.rir != null && this.rir < 0) throw new InvalidSetMetricsException("El RIR no puede ser negativo.");
    }

    public static SetLog createNew(Long exerciseId, Integer setNumber, Integer repsPerformed,
                                   BigDecimal weightLifted, Integer rir, String notes) {
        return new SetLog(null, exerciseId, setNumber, repsPerformed, weightLifted, rir, notes);
    }

    public static SetLog restore(Long id, Long exerciseId, Integer setNumber, Integer repsPerformed,
                                 BigDecimal weightLifted, Integer rir, String notes) {
        return new SetLog(id, exerciseId, setNumber, repsPerformed, weightLifted, rir, notes);
    }
}