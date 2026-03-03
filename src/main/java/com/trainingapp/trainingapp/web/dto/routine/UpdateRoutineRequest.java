package com.trainingapp.trainingapp.web.dto.routine;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record UpdateRoutineRequest(
        @NotBlank(message = "The routine name cannot be empty")
        String name,

        Long trainerId,

        @NotEmpty(message = "The routine should include at least one day of training")
        @Valid
        List<UpdateTrainingDayRequest> days
) {
    public record UpdateTrainingDayRequest(
            Long id,

            @NotBlank(message = "The name of the day cannot be empty")
            String dayName,

            @NotEmpty(message = "The day should include at least one exercise")
            @Valid
            List<UpdateRoutineDetailRequest> exercises
    ){}
    public record UpdateRoutineDetailRequest(
            // ID opcional (por si un ejercicio es nuevo)
            Long id,

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
    ) {}
}