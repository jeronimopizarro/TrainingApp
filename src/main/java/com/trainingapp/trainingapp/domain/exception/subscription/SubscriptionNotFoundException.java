package com.trainingapp.trainingapp.domain.exception.subscription;

public class SubscriptionNotFoundException extends RuntimeException {
    public SubscriptionNotFoundException(String message) {
        super(message);
    }
}