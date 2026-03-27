package com.trainingapp.trainingapp.domain.exception.routine;

public class ActiveRoutineRequestAlreadyExistsException extends RuntimeException {
    public ActiveRoutineRequestAlreadyExistsException(Long memberId) {
        super("El socio con ID " + memberId + " ya tiene una solicitud de rutina pendiente.");
    }
}