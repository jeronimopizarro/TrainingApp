package com.trainingapp.trainingapp.web.dto.exercise;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record UpdateExerciseRequest(
        @NotBlank(message = "The exercise name cannot be empty")
        String name,

        String description,
        String imageUrl,
        String videoUrl,
        Boolean isBase,

        @NotEmpty(message = "You must assign at least one muscle group")
        List<MuscleGroupAssignmentRequest> muscleGroups
) {
    public record MuscleGroupAssignmentRequest(
            @NotNull(message = "Muscle group ID is required")
            Long muscleGroupId,

            @NotNull(message = "You must specify if it is the primary muscle")
            Boolean isPrimary
    ) {}
}