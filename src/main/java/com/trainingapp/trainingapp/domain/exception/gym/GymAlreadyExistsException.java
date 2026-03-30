package com.trainingapp.trainingapp.domain.exception.gym;

public class GymAlreadyExistsException extends RuntimeException {
    public GymAlreadyExistsException(String name) {
        super("Ya existe un gimnasio activo con el nombre: " + name);
    }
}
