package com.trainingapp.trainingapp.domain.exception.subscription;

public class ActiveSubscriptionAlreadyExistsException extends RuntimeException {
    public ActiveSubscriptionAlreadyExistsException(String message) {
        super(message);
    }
}