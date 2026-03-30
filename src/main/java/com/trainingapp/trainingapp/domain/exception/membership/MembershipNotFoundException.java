package com.trainingapp.trainingapp.domain.exception.membership;

public class MembershipNotFoundException extends RuntimeException {
    public MembershipNotFoundException(Long id) {
        super("El plan de membresía con el ID " + id + " no fue encontrado.");
    }
}