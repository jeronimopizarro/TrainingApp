package com.trainingapp.trainingapp.domain.exception.muscleGroup;

public class MuscleGroupNotFoundException extends RuntimeException {
    public MuscleGroupNotFoundException(Long id) {
        super("Muscle group with ID " + id + " does not exist.");
    }
}
