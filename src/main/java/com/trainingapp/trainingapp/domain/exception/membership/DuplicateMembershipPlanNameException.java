package com.trainingapp.trainingapp.domain.exception.membership;

public class DuplicateMembershipPlanNameException extends RuntimeException {
    public DuplicateMembershipPlanNameException(String name) {
        super("Ya existe otro plan activo con el nombre '" + name + "' en tu gimnasio.");
    }
}
