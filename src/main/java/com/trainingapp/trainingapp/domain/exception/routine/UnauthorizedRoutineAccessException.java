package com.trainingapp.trainingapp.domain.exception.routine;

public class UnauthorizedRoutineAccessException extends RuntimeException {
    public UnauthorizedRoutineAccessException() {
        super("Acceso denegado: Solo puedes consultar tus propias rutinas o las correspondientes a tu rol en tu gimnasio.");
    }
}
