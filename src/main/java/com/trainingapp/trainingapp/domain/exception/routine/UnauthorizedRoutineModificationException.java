package com.trainingapp.trainingapp.domain.exception.routine;

public class UnauthorizedRoutineModificationException extends RuntimeException {
    public UnauthorizedRoutineModificationException() {
        super("Acceso denegado: No tienes permiso para modificar esta rutina. Solo puedes editar tus propias rutinas o las que te fueron asignadas.");
    }
}
