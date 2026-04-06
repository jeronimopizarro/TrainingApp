package com.trainingapp.trainingapp.domain.exception.routine;

public class InvalidRoutineRequestStateException extends RuntimeException {
    public InvalidRoutineRequestStateException(String message) {
        super(message);
    }
}
