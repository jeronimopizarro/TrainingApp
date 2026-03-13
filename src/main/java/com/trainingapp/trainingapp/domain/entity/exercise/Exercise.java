package com.trainingapp.trainingapp.domain.entity.exercise;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Exercise {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String videoUrl;
    private Boolean isBase;
    private Long createdByUserId;
    private Long gymId;
    private boolean active;
    private List<ExerciseMuscleGroup> muscleGroups;

    public Exercise(String name, String description, String imageUrl, String videoUrl,
                    Boolean isBase, Long createdByUserId, Long gymId) {
        validateName(name);

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
        this.active = true;
        this.muscleGroups = new ArrayList<>();
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("The exercise name cannot be empty.");
        }
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

    public void updateDetails(String name, String description, String imageUrl, String videoUrl) {
        if (name != null) {
            validateName(name);
            this.name = name;
        }

        if (description != null) this.description = description;
        if (imageUrl != null) this.imageUrl = imageUrl;
        if (videoUrl != null) this.videoUrl = videoUrl;
    }

    public void clearMuscleGroups() {
        this.muscleGroups.clear();
    }

    public void deactivate() {
        if (!this.active) throw new IllegalStateException("El ejercicio ya está inactivo.");
        this.active = false;
    }

    public void activate() {
        if (this.active) throw new IllegalStateException("El ejercicio ya está activo.");
        this.active = true;
    }

    public void setIsBase(Boolean isBase) {
        if (isBase != null) {
            this.isBase = isBase;
        }
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setMuscleGroups(List<ExerciseMuscleGroup> muscleGroups) {
        this.muscleGroups = muscleGroups;
    }
}