package com.trainingapp.trainingapp.domain.exception.routine;

public class InvalidRoutineDatesException extends RuntimeException {
    public InvalidRoutineDatesException() {
        super("La fecha de fin no puede ser anterior a la fecha de inicio.");
    }
}
