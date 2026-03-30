package com.trainingapp.trainingapp.domain.exception.routine;

public class InvalidRoutineStateException extends RuntimeException {
    public InvalidRoutineStateException() {
        super("Solo se pueden modificar rutinas en estado DRAFT.");
    }
}
