package com.trainingapp.trainingapp.application.useCase.user;

public class ReceptionistNotFoundException extends RuntimeException {
    public ReceptionistNotFoundException(Long id) {
        super("No se encontró el recepcionista con ID: " + id);
    }
}