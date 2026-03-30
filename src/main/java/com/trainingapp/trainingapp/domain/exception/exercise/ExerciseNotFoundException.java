package com.trainingapp.trainingapp.domain.exception.exercise;

public class ExerciseNotFoundException extends RuntimeException {
    public ExerciseNotFoundException(Long id) {
        super("El ejercicio con el ID" + id + " no fue encontrado.");
    }
}
