package com.trainingapp.trainingapp.domain.entity.exercise;

import com.trainingapp.trainingapp.domain.exception.exercise.CustomExerciseRequiresGymException;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseAlreadyActiveException;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseAlreadyInactiveException;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseNameRequiredException;
import com.trainingapp.trainingapp.domain.exception.muscleGroup.DuplicateMuscleGroupException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Exercise {

    private final Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String videoUrl;
    private Boolean isBase;
    private final Long createdByUserId;
    private Long gymId;
    private boolean active;
    private List<ExerciseMuscleGroup> muscleGroups;

    private Exercise(Long id, String name, String description, String imageUrl, String videoUrl,
                     Boolean isBase, Long createdByUserId, Long gymId, boolean active,
                     List<ExerciseMuscleGroup> muscleGroups) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.isBase = isBase != null ? isBase : false;
        this.createdByUserId = createdByUserId;
        this.gymId = gymId;
        this.active = active;
        this.muscleGroups = muscleGroups != null ? new ArrayList<>(muscleGroups) : new ArrayList<>();
        validate();
    }

    private void validate() {
        if (this.name == null || this.name.isBlank()) {
            throw new ExerciseNameRequiredException();
        }
        if (!this.isBase && this.gymId == null) {
            throw new CustomExerciseRequiresGymException();
        }
    }

    public static Exercise createNew(String name, String description, String imageUrl, String videoUrl,
                                     Boolean isBase, Long createdByUserId, Long gymId) {
        return new Exercise(null, name, description, imageUrl, videoUrl, isBase, createdByUserId, gymId, true, new ArrayList<>());
    }

    public static Exercise restore(Long id, String name, String description, String imageUrl, String videoUrl,
                                   Boolean isBase, Long createdByUserId, Long gymId, boolean active,
                                   List<ExerciseMuscleGroup> muscleGroups) {
        return new Exercise(id, name, description, imageUrl, videoUrl, isBase, createdByUserId, gymId, active, muscleGroups);
    }

    public void addMuscleGroup(Long muscleGroupId, boolean isPrimary) {
        boolean alreadyExists = this.muscleGroups.stream()
                .anyMatch(mg -> mg.getMuscleGroupId().equals(muscleGroupId));

        if (alreadyExists) {
            throw new DuplicateMuscleGroupException();
        }

        if (isPrimary) {
            this.muscleGroups.forEach(ExerciseMuscleGroup::makeSecondary);
        }

        this.muscleGroups.add(ExerciseMuscleGroup.create(muscleGroupId, isPrimary));
    }

    public void updateDetails(String name, String description, String imageUrl, String videoUrl) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (imageUrl != null) this.imageUrl = imageUrl;
        if (videoUrl != null) this.videoUrl = videoUrl;

        validate();
    }

    public void updateBaseStatus(Boolean isBase) {
        if (isBase != null) {
            this.isBase = isBase;
            if (this.isBase) {
                this.gymId = null;
            }
            validate();
        }
    }

    public void clearMuscleGroups() {
        this.muscleGroups.clear();
    }

    public void deactivate() {
        if (!this.active) throw new ExerciseAlreadyInactiveException();
        this.active = false;
    }

    public void activate() {
        if (this.active) throw new ExerciseAlreadyActiveException();
        this.active = true;
    }
}