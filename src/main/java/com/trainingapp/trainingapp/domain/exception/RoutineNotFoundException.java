package com.trainingapp.trainingapp.domain.exception;

public class RoutineNotFoundException extends RuntimeException {
    public RoutineNotFoundException(String message) {
        super(message);
    }
}
