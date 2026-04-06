package com.trainingapp.trainingapp.domain.exception.membership;

public class MembershipAlreadyInactiveException extends RuntimeException {
    public MembershipAlreadyInactiveException() {
        super("El plan de membresía ya se encuentra inactivo.");
    }
}
