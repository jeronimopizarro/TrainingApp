package com.trainingapp.trainingapp.domain.exception.user;

public class AdminNotFoundException extends RuntimeException {
    public AdminNotFoundException(Long id) {
        super("El administrador con ID " + id + " no fue encontrado.");
    }
}