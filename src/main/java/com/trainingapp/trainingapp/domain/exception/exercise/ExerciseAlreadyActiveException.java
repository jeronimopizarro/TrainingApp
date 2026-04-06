package com.trainingapp.trainingapp.domain.exception.exercise;

public class ExerciseAlreadyActiveException extends RuntimeException {
    public ExerciseAlreadyActiveException() {
        super("El ejercicio ya se encuentra activo.");
    }
}
