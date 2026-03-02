package com.trainingapp.trainingapp.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DuplicateRoutineRequest(

        @NotBlank(message = "The new routine name cannot be empty")
        String newName,

        @NotNull(message = "The target member ID is mandatory")
        Long targetMemberId,

        Long trainerId,

        @NotNull(message = "The created by user ID is mandatory")
        Long createdByUserId
) {
}
