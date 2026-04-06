package com.trainingapp.trainingapp.domain.exception.membership;

public class MembershipPlanNameRequiredException extends RuntimeException {
    public MembershipPlanNameRequiredException() {
        super("El nombre de la membresía es obligatorio y no puede estar vacío.");
    }
}
