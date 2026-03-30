package com.trainingapp.trainingapp.domain.exception.routine;

public class RoutineNotFoundException extends RuntimeException {
    public RoutineNotFoundException(Long id) {
        super("Routine with id " + id + " not found.");
    }
}
