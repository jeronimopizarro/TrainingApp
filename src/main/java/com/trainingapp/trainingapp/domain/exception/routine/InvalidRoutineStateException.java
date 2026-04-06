package com.trainingapp.trainingapp.domain.exception.routine;

public class InvalidRoutineStateException extends RuntimeException {
    public InvalidRoutineStateException() {
        super("La rutina no se encuentra en un estado válido para esta operación.");
    }

    public InvalidRoutineStateException(String message) {
        super(message);
    }
}
