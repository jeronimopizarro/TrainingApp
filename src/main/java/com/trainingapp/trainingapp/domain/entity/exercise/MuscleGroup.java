package com.trainingapp.trainingapp.domain.entity.exercise;

import com.trainingapp.trainingapp.domain.exception.muscleGroup.MuscleGroupNameRequiredException;
import lombok.Getter;

@Getter
public class MuscleGroup {

    private final Long id;
    private String name;
    private String description;

    public  MuscleGroup(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        validate();
    }

    private void validate() {
        if (this.name == null || this.name.trim().isEmpty()) {
            throw new MuscleGroupNameRequiredException();
        }
    }

    public static MuscleGroup createNew(String name, String description) {
        return new MuscleGroup(null, name, description);
    }

    public static MuscleGroup restore(Long id, String name, String description) {
        return new MuscleGroup(id, name, description);
    }

    // 3. Comportamiento y Validación
    public void updateDetails(String name, String description) {
        this.name = name;
        this.description = description;
        validate();
    }

}