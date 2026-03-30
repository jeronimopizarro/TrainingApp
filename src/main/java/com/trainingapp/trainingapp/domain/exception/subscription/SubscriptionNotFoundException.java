package com.trainingapp.trainingapp.domain.exception.subscription;

public class SubscriptionNotFoundException extends RuntimeException {
    public SubscriptionNotFoundException(Long id) {
        super("La subscripción con el ID " + id + " no fue encontrada.");
    }
}