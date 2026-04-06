package com.trainingapp.trainingapp.domain.exception.routine;

public class RoutineNameRequiredException extends RuntimeException {
    public RoutineNameRequiredException() {
        super("El nombre de la rutina es obligatorio.");
    }
}