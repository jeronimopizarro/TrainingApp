package com.trainingapp.trainingapp.domain.exception.gym;

public class UnauthorizedGymAccessException extends RuntimeException {
    public UnauthorizedGymAccessException() {
        super("Acceso denegado: El recurso solicitado pertenece a otro gimnasio.");
    }
}
