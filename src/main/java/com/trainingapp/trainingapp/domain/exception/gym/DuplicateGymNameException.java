package com.trainingapp.trainingapp.domain.exception.gym;

public class DuplicateGymNameException extends RuntimeException {
    public DuplicateGymNameException(String name) {
        super("El gimnasio con el nombre " + name + " ya existe.");
    }
}
