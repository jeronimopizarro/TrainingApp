package com.trainingapp.trainingapp.domain.exception.routine;

public class RoutineNotFoundException extends RuntimeException {
    public RoutineNotFoundException(String message) {
        super(message);
    }
}
