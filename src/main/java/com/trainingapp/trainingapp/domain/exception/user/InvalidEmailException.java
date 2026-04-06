package com.trainingapp.trainingapp.domain.exception.user;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException() {
        super("El formato del correo electrónico es inválido o está vacío.");
    }
}
