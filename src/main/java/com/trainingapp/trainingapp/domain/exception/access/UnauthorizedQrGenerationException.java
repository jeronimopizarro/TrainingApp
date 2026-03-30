package com.trainingapp.trainingapp.domain.exception.access;

public class UnauthorizedQrGenerationException extends RuntimeException {
    public UnauthorizedQrGenerationException() {
        super("Acceso denegado: No puedes generar un código de acceso para otro socio.");
    }
}
