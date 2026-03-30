package com.trainingapp.trainingapp.domain.exception.exercise;

public class BaseExerciseAlreadyExistsException extends RuntimeException {
    public BaseExerciseAlreadyExistsException(String name) {
        super("Ya existe un ejercicio base llamado " + name);
    }
}
