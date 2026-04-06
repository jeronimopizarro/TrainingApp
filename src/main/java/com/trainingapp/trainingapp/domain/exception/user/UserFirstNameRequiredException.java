package com.trainingapp.trainingapp.domain.exception.user;

public class UserFirstNameRequiredException extends RuntimeException {
    public UserFirstNameRequiredException() {
        super("El nombre del usuario es obligatorio.");
    }
}
