package com.trainingapp.trainingapp.domain.exception.gym;

public class DuplicateGymNameException extends RuntimeException {
    public DuplicateGymNameException(String message) {
        super(message);
    }
}
