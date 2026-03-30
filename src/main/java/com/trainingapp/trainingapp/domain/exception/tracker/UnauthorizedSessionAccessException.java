package com.trainingapp.trainingapp.domain.exception.tracker;

public class UnauthorizedSessionAccessException extends RuntimeException {
    public UnauthorizedSessionAccessException() {
        super("Acceso denegado: No puedes interactuar con una sesión que no te pertenece.");
    }
}
