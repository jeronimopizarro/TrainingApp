package com.trainingapp.trainingapp.domain.exception.user;

public class TrainerNotFoundException extends RuntimeException {
    public TrainerNotFoundException(Long trainerId) {
        super("No se encontró el entrenador con ID: " + trainerId);
    }
}