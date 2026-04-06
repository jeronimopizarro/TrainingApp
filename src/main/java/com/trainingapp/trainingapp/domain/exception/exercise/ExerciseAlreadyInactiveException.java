package com.trainingapp.trainingapp.domain.exception.exercise;

public class ExerciseAlreadyInactiveException extends RuntimeException {
    public ExerciseAlreadyInactiveException() {
        super("El ejercicio ya se encuentra inactivo.");
    }
}
