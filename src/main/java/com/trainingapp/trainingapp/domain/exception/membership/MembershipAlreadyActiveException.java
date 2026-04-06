package com.trainingapp.trainingapp.domain.exception.membership;

public class MembershipAlreadyActiveException extends RuntimeException {
    public MembershipAlreadyActiveException() {
        super("El plan de membresía ya se encuentra activo.");
    }
}
