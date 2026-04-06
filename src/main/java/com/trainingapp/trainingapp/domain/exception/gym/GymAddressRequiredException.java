package com.trainingapp.trainingapp.domain.exception.gym;

public class GymAddressRequiredException extends RuntimeException {
    public GymAddressRequiredException() {
        super("La dirección del gimnasio es obligatoria.");
    }
}
