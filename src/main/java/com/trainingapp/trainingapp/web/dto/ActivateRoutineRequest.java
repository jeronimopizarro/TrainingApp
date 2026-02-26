package com.trainingapp.trainingapp.web.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ActivateRoutineRequest(

        @NotNull(message = "El ID del usuario que activa la rutina es obligatorio")
        Long requestingUserId,

        @NotNull(message = "La fecha de inicio es obligatoria")
        @FutureOrPresent(message = "La fecha de inicio no puede ser en el pasado")
        LocalDate startDate,

        LocalDate endDate
) {
}
