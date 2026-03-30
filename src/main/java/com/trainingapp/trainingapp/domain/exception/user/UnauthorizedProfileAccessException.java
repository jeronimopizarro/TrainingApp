package com.trainingapp.trainingapp.domain.exception.user;

public class UnauthorizedProfileAccessException extends RuntimeException {
    public UnauthorizedProfileAccessException() {
        super("Acceso denegado: No tienes permisos para ver el perfil de este usuario.");
    }
}
