package com.trainingapp.trainingapp.domain.exception.tracker;

public class TrainingRequiresActiveSubscriptionException extends RuntimeException {
    public TrainingRequiresActiveSubscriptionException() {
        super("Acceso denegado: No puedes iniciar un entrenamiento sin una membresía activa.");
    }
}
