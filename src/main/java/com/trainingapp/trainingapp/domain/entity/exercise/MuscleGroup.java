package com.trainingapp.trainingapp.domain.entity.exercise;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MuscleGroup {

    private Long id;
    private String name;
    private String description;

    public  MuscleGroup(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("The muscle group name cannot be empty.");
        }
        this.name = name;
        this.description = description;
    }
}