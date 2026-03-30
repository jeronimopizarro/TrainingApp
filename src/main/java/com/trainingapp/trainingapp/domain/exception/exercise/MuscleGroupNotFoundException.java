package com.trainingapp.trainingapp.domain.exception.exercise;

public class MuscleGroupNotFoundException extends RuntimeException {
    public MuscleGroupNotFoundException(Long id) {
        super("The muscle group with id " + id + " was not found.");
    }
}