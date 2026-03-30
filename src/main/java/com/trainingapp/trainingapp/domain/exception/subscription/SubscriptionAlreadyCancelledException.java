package com.trainingapp.trainingapp.domain.exception.subscription;

public class SubscriptionAlreadyCancelledException extends RuntimeException {
    public SubscriptionAlreadyCancelledException() {
        super("La suscripción ya se encuentra cancelada.");
    }
}
