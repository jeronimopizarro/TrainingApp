package com.trainingapp.trainingapp.domain.exception.gym;

public class GymNotFoundException extends RuntimeException {
    public GymNotFoundException(String message) {
        super(message);
    }
}
