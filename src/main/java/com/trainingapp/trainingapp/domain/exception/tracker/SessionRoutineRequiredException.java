package com.trainingapp.trainingapp.domain.exception.tracker;

public class SessionRoutineRequiredException extends RuntimeException {
    public SessionRoutineRequiredException() {
        super("La sesión debe estar vinculada a una rutina.");
    }
}