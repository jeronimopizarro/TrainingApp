package com.trainingapp.trainingapp.domain.exception.routine;

public class TrainingDayNameRequiredException extends RuntimeException {
    public TrainingDayNameRequiredException() {
        super("El nombre del día de entrenamiento es obligatorio.");
    }
}
