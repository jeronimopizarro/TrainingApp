package com.trainingapp.trainingapp.domain.exception.gym;

public class GymNameRequiredException extends RuntimeException {
    public GymNameRequiredException() {
        super("El nombre del gimnasio es obligatorio y no puede estar vacío.");
    }
}
