package com.trainingapp.trainingapp.domain.exception.routine;

public class ActiveRoutineAlreadyExistsException extends RuntimeException {
    public ActiveRoutineAlreadyExistsException() {
        super("El socio ya tiene una rutina activa. Debe marcarla como completada o inactiva antes de iniciar una nueva.");
    }
}
