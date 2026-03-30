package com.trainingapp.trainingapp.domain.exception.subscription;

public class ActiveSubscriptionNotFoundException extends RuntimeException {
    public ActiveSubscriptionNotFoundException(Long id) {
        super("La suscripción activa del miembro con el ID " + id + " no fue encontrada.");
    }
}