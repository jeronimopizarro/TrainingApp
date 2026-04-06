package com.trainingapp.trainingapp.domain.exception.user;

public class UserAlreadyInactiveException extends RuntimeException {
    public UserAlreadyInactiveException() {
        super("El usuario ya se encuentra inactivo.");
    }
}
