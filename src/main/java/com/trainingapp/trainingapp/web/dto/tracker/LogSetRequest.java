package com.trainingapp.trainingapp.web.dto.tracker;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LogSetRequest(
        @NotNull(message = "El ejercicio es obligatorio")
        Long exerciseId,

        @NotNull(message = "El número de serie es obligatorio")
        @Min(value = 1, message = "El número de serie debe ser mayor a 0")
        Integer setNumber,

        @NotNull(message = "Las repeticiones son obligatorias")
        @Min(value = 0, message = "Las repeticiones no pueden ser negativas")
        Integer repsPerformed,

        @NotNull(message = "El peso es obligatorio")
        @Min(value = 0, message = "El peso no puede ser negativo")
        BigDecimal weightLifted,

        String notes
) {
}