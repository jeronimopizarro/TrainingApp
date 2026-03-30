package com.trainingapp.trainingapp.domain.exception.tracker;

public class ActiveSessionAlreadyExistsException extends RuntimeException {
    public ActiveSessionAlreadyExistsException(Long sessionId) {
        super("Ya tienes un entrenamiento en progreso (ID: " + sessionId + "). Finalízalo antes de iniciar uno nuevo.");
    }
}
