package com.trainingapp.trainingapp.domain.exception.routine;

public class RoutineRequestNotFoundException extends RuntimeException {
    public RoutineRequestNotFoundException(Long id) {
        super("Routine request with id " + id + " not found.");
    }
}
