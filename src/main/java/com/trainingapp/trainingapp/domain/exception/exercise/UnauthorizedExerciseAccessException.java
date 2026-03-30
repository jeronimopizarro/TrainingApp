package com.trainingapp.trainingapp.domain.exception.exercise;

public class UnauthorizedExerciseAccessException extends RuntimeException {
    public UnauthorizedExerciseAccessException() {
        super("Acceso denegado: no podes interactuar con ejercicios de otro gimnasio.");
    }
}
