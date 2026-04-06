package com.trainingapp.trainingapp.domain.exception.gym;

public class GymAlreadyActiveException extends RuntimeException {
    public GymAlreadyActiveException() {
        super("El gimnasio ya se encuentra activo.");
    }
}
