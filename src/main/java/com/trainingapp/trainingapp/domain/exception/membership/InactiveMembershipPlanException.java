package com.trainingapp.trainingapp.domain.exception.membership;

public class InactiveMembershipPlanException extends RuntimeException {
    public InactiveMembershipPlanException() {
        super("El plan de membresía seleccionado está inactivo y no puede ser asignado.");
    }
}
