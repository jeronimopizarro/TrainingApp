package com.trainingapp.trainingapp.domain.exception.routine;

public class RoutineMemberRequiredException extends RuntimeException {
    public RoutineMemberRequiredException() {
        super("La rutina debe tener un socio asignado.");
    }
}
