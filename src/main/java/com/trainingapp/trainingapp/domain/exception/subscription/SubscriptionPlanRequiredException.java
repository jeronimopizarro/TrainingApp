package com.trainingapp.trainingapp.domain.exception.subscription;

public class SubscriptionPlanRequiredException extends RuntimeException {
    public SubscriptionPlanRequiredException() {
        super("La suscripción debe tener un plan asociado.");
    }
}