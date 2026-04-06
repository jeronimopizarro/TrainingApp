package com.trainingapp.trainingapp.domain.exception.subscription;

public class InvalidSubscriptionDurationException extends RuntimeException {
    public InvalidSubscriptionDurationException() {
        super("La duración del plan debe ser mayor a cero.");
    }
}