package com.trainingapp.trainingapp.domain.exception.gym;

public class GymNotFoundException extends RuntimeException {
    public GymNotFoundException(Long id) {
        super("El gimnasio con el ID " + id + " no fue encontrado.");
    }
}
