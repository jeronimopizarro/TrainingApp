package com.trainingapp.trainingapp.domain.exception.auth;

public class UnauthenticatedUserException extends RuntimeException {
    public UnauthenticatedUserException() {
        super("Usuario no encontrado o sesión inválida. Por favor, vuelva a iniciar sesión.");
    }
}
