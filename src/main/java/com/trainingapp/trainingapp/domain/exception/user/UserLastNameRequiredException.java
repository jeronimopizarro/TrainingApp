package com.trainingapp.trainingapp.domain.exception.user;

public class UserLastNameRequiredException extends RuntimeException {
    public UserLastNameRequiredException() {
        super("El apellido del usuario es obligatorio.");
    }
}
