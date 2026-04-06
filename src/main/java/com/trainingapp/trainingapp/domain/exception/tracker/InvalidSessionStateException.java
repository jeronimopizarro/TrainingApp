package com.trainingapp.trainingapp.domain.exception.tracker;

public class InvalidSessionStateException extends RuntimeException {
    public InvalidSessionStateException(String message) {
        super(message);
    }
}
