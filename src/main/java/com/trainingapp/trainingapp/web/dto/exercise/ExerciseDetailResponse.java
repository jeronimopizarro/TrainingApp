package com.trainingapp.trainingapp.web.dto.exercise;

import java.util.List;

public record ExerciseDetailResponse(
        Long id,
        String name,
        String description,
        String imageUrl,
        String videoUrl,
        Boolean isBase,
        Long creatorTrainerId,
        List<MuscleGroupDetail> muscleGroups
) {
    public record MuscleGroupDetail(
            Long muscleGroupId,
            Boolean isPrimary
    ) {}
}
