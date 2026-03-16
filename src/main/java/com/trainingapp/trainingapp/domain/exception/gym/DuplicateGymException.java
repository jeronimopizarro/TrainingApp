package com.trainingapp.trainingapp.domain.exception.gym;

public class DuplicateGymException extends RuntimeException {
    public DuplicateGymException(String message) {
        super(message);
    }
}
