package com.trainingapp.trainingapp.domain.entity.exercise;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ExerciseMuscleGroup {

    private final Long muscleGroupId;
    private boolean isPrimary;

    private ExerciseMuscleGroup(Long muscleGroupId, boolean isPrimary) {
        this.muscleGroupId = muscleGroupId;
        this.isPrimary = isPrimary;
    }

    public static ExerciseMuscleGroup create(Long muscleGroupId, boolean isPrimary) {
        return new ExerciseMuscleGroup(muscleGroupId, isPrimary);
    }

    public void makeSecondary() {
        this.isPrimary = false;
    }
}