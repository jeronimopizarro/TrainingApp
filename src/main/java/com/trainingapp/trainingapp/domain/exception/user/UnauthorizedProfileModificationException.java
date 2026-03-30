package com.trainingapp.trainingapp.domain.exception.user;

public class UnauthorizedProfileModificationException extends RuntimeException {
    public UnauthorizedProfileModificationException() {
        super("Acceso denegado: Solo tienes permiso para modificar tu propio perfil.");
    }
}
