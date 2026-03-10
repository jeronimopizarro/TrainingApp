package com.trainingapp.trainingapp.domain.exception.user;

public class TrainerNotFoundException extends RuntimeException {
    public TrainerNotFoundException(String message) {
        super(message);
    }
}