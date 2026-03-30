package com.trainingapp.trainingapp.domain.exception.exercise;

public class UnauthorizedBaseExerciseModificationException extends RuntimeException {
    public UnauthorizedBaseExerciseModificationException() {
        super("Acceso denegado: No tienes permisos para modificar o eliminar ejercicios base del sistema.");
    }
}
