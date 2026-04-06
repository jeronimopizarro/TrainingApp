package com.trainingapp.trainingapp.domain.exception.user;

public class UserAlreadyActiveException extends RuntimeException {
    public UserAlreadyActiveException() {
        super("El usuario ya se encuentra activo.");
    }
}
