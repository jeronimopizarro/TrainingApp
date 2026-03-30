package com.trainingapp.trainingapp.domain.exception.exercise;

public class UnauthorizedExerciseModificationException extends RuntimeException {
    public UnauthorizedExerciseModificationException() {
        super("Acceso denegado: Solo puedes modificar o eliminar los ejercicios creados por ti.");
    }
}
