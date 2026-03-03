package com.trainingapp.trainingapp.domain.entity.exercise;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExerciseMuscleGroup {

    private Long muscleGroupId;
    private boolean isPrimary;

    public ExerciseMuscleGroup(Long muscleGroupId, boolean isPrimary) {
        if (muscleGroupId == null || muscleGroupId <= 0) {
            throw new IllegalArgumentException("Muscle group id mus be valid.");
        }
        this.muscleGroupId = muscleGroupId;
        this.isPrimary = isPrimary;
    }
}