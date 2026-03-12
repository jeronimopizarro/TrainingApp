package com.trainingapp.trainingapp.domain.entity.exercise;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Exercise {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String videoUrl;
    private Boolean isBase;

    private Long createdByUserId;
    private Long gymId;

    private List<ExerciseMuscleGroup> muscleGroups;

    public Exercise(String name, String description, String imageUrl, String videoUrl,
                    Boolean isBase, Long createdByUserId, Long gymId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("The exercise name cannot be empty.");
        }

        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.isBase = isBase != null ? isBase : false;

        if (!this.isBase && gymId == null) {
            throw new IllegalArgumentException("A custom exercise must be associated with a Gym.");
        }

        this.createdByUserId = createdByUserId;
        this.gymId = gymId;

        this.muscleGroups = new ArrayList<>();
    }

    public void addMuscleGroup(Long muscleGroupId, boolean isPrimary) {
        boolean alreadyExists = this.muscleGroups.stream().anyMatch(
                mg -> mg.getMuscleGroupId().equals(muscleGroupId));

        if (alreadyExists) {
            throw new IllegalArgumentException("This muscle group is already assigned to the exercise.");
        }

        if (isPrimary) {
            this.muscleGroups.forEach(mg -> mg.setPrimary(false));
        }

        this.muscleGroups.add(new ExerciseMuscleGroup(muscleGroupId, isPrimary));
    }

    public void clearMuscleGroups() {
        this.muscleGroups.clear();
    }
}