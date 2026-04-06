package com.trainingapp.trainingapp.domain.exception.user;

public class UserDniRequiredException extends RuntimeException {
    public UserDniRequiredException() {
        super("El DNI del usuario es obligatorio.");
    }
}
