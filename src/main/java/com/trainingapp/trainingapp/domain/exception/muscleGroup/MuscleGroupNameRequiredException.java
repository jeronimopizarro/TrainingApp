package com.trainingapp.trainingapp.domain.exception.muscleGroup;

public class MuscleGroupNameRequiredException extends RuntimeException {
    public MuscleGroupNameRequiredException() {
        super("El nombre del grupo muscular es obligatorio.");
    }
}