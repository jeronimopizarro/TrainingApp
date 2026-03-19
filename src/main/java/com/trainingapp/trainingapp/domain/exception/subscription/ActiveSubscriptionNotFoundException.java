package com.trainingapp.trainingapp.domain.exception.subscription;

public class ActiveSubscriptionNotFoundException extends RuntimeException {
    public ActiveSubscriptionNotFoundException(String message) {
        super(message);
    }
}