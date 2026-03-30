package com.trainingapp.trainingapp.domain.exception.subscription;

import java.time.LocalDate;

public class ActiveSubscriptionAlreadyExistsException extends RuntimeException {
    public ActiveSubscriptionAlreadyExistsException(LocalDate endDate) {
        super("El socio ya posee una suscripción activa que vence el: " + endDate);
    }
}