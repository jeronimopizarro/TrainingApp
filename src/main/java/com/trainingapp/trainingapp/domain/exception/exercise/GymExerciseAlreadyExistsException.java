package com.trainingapp.trainingapp.domain.exception.exercise;

public class GymExerciseAlreadyExistsException extends RuntimeException {
    public GymExerciseAlreadyExistsException(String name) {
        super("Ya existe un ejercicio llamado " + name + " en tu gimnasio.");
    }
}
