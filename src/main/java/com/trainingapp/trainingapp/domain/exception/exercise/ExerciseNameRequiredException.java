package com.trainingapp.trainingapp.domain.exception.exercise;

public class ExerciseNameRequiredException extends RuntimeException {
    public ExerciseNameRequiredException() {
        super("El nombre del ejercicio es obligatorio.");
    }
}