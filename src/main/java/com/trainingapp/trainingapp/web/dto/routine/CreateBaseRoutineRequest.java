package com.trainingapp.trainingapp.web.dto.routine;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateBaseRoutineRequest(
        @NotBlank(message = "El nombre de la rutina base no puede estar vacío")
        String name,

        @NotEmpty(message = "La rutina debe tener al menos un día de entrenamiento")
        @Valid
        List<CreateTrainingDayRequest> days
) {
    public record CreateTrainingDayRequest(
            @NotBlank(message = "El nombre del día no puede estar vacío")
            String dayName,

            @NotEmpty(message = "El día debe tener al menos un ejercicio")
            @Valid
            List<CreateRoutineDetailRequest> exercises
    ) {}

    public record CreateRoutineDetailRequest(
            @NotNull(message = "El ID del ejercicio es obligatorio") Long exerciseId,
            @NotNull(message = "La cantidad de series es obligatoria") @Min(1) Integer sets,
            @NotNull(message = "Las repeticiones mínimas son obligatorias") @Min(1) Integer repsMin,
            @NotNull(message = "Las repeticiones máximas son obligatorias") @Min(1) Integer repsMax,
            @NotNull(message = "El RIR objetivo es obligatorio") @Min(0) Integer targetRIR,
            @NotNull(message = "El peso sugerido es obligatorio") @Min(0) Double suggestedWeight,
            String notes
    ) {}
}
