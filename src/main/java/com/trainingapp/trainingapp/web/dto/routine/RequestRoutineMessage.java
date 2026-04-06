package com.trainingapp.trainingapp.web.dto.routine;

import com.trainingapp.trainingapp.domain.enums.routine.ExperienceLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RequestRoutineMessage(

        Long targetTrainerId, // Opcional: Puede ser null si le da igual qué profe le arme la rutina

        @NotNull(message = "Debe especificar la cantidad de días disponibles")
        @Min(value = 1, message = "Debe tener al menos 1 día disponible")
        @Max(value = 7, message = "No puede exceder los 7 días")
        Integer availableDays,

        @NotNull(message = "El nivel de experiencia es obligatorio")
        ExperienceLevel experienceLevel,

        String injuries, // Opcional: Puede venir vacío o null si está sano

        @NotBlank(message = "El objetivo principal es obligatorio")
        String primaryGoal
) {}