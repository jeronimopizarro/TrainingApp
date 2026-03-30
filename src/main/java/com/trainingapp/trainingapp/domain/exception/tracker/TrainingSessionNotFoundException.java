package com.trainingapp.trainingapp.domain.exception.tracker;

public class TrainingSessionNotFoundException extends RuntimeException {
    public TrainingSessionNotFoundException(Long id) {
        super("La sesión de entrenamiento con ID " + id + " no fue encontrada.");
    }
}
