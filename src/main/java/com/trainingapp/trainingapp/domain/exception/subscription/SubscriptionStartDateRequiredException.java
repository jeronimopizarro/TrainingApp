package com.trainingapp.trainingapp.domain.exception.subscription;

public class SubscriptionStartDateRequiredException extends RuntimeException {
    public SubscriptionStartDateRequiredException() {
        super("La fecha de inicio no puede ser nula.");
    }
}