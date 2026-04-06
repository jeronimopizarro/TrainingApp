package com.trainingapp.trainingapp.domain.exception.gym;

public class GymAlreadyInactiveException extends RuntimeException {
    public GymAlreadyInactiveException() {
        super("El gimnasio ya se encuentra inactivo.");
    }
}
