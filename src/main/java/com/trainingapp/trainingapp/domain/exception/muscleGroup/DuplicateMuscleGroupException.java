package com.trainingapp.trainingapp.domain.exception.muscleGroup;

public class DuplicateMuscleGroupException extends RuntimeException {
    public DuplicateMuscleGroupException() {
        super("Este grupo muscular ya está asignado al ejercicio.");
    }
}
