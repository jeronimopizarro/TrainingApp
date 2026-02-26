package com.trainingapp.trainingapp.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateRoutineRequest(
        @NotBlank(message = "El nombre de la rutina no puede estar vacío")
        String name,

        @NotNull(message = "El ID del socio es obligatorio")
        Long memberId,

        Long trainerId,

        @NotNull(message = "El ID del usuario creador es obligatorio")
        Long createdByUserId,

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
    ) {
    }

    public record CreateRoutineDetailRequest(
            @NotNull(message = "El ID del ejercicio es obligatorio")
            Long exerciseId,

            @NotNull(message = "La cantidad de series es obligatoria")
            @Min(value = 1, message = "Debe tener al menos 1 serie")
            Integer sets,

            @NotNull(message = "Las repeticiones mínimas son obligatorias")
            @Min(value = 1, message = "Debe tener al menos 1 repetición")
            Integer repsMin,

            @NotNull(message = "Las repeticiones máximas son obligatorias")
            @Min(value = 1, message = "Debe tener al menos 1 repetición")
            Integer repsMax,

            @NotNull(message = "El RIR objetivo es obligatorio")
            @Min(value = 0, message = "El RIR no puede ser negativo")
            Integer targetRIR,

            @NotNull(message = "El peso sugerido es obligatorio")
            @Min(value = 0, message = "El peso no puede ser negativo")
            Double suggestedWeight,

            String notes
    ) {
    }
}