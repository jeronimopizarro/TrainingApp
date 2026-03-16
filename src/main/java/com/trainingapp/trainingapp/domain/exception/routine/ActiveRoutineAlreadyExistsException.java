package com.trainingapp.trainingapp.domain.exception.routine;

public class ActiveRoutineAlreadyExistsException extends RuntimeException {
    public ActiveRoutineAlreadyExistsException(String message) {
        super(message);
    }
}
