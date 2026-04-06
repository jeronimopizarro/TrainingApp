package com.trainingapp.trainingapp.domain.exception.exercise;

public class CustomExerciseRequiresGymException extends RuntimeException {
    public CustomExerciseRequiresGymException() {
        super("Un ejercicio personalizado debe estar asociado a un gimnasio.");
    }
}
