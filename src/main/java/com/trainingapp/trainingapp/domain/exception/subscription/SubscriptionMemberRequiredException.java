package com.trainingapp.trainingapp.domain.exception.subscription;

public class SubscriptionMemberRequiredException extends RuntimeException {
    public SubscriptionMemberRequiredException() {
        super("La suscripción debe tener un socio asociado.");
    }
}