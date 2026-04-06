package com.trainingapp.trainingapp.domain.exception.user;

public class UserPasswordRequiredException extends RuntimeException {
    public UserPasswordRequiredException() {
        super("La contraseña es obligatoria.");
    }
}
