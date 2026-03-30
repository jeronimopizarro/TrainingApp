package com.trainingapp.trainingapp.domain.exception.subscription;

public class InvalidSubscriptionStartDateException extends RuntimeException {
    public InvalidSubscriptionStartDateException() {
        super("La fecha de inicio de la suscripción no puede ser anterior al día de hoy.");
    }
}
