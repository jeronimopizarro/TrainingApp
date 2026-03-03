package com.trainingapp.trainingapp.domain.exception.exercise;

public class MuscleGroupNotFoundException extends RuntimeException {
    public MuscleGroupNotFoundException(String message) {
        super(message);
    }
}