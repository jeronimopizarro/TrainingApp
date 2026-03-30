package com.trainingapp.trainingapp.domain.exception.subscription;

public class SubscriptionAlreadyExpiredException extends RuntimeException {
    public SubscriptionAlreadyExpiredException() {
        super("No se puede cancelar una suscripción que ya ha vencido.");
    }
}
