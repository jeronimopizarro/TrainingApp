package com.trainingapp.trainingapp.domain.exception.tracker;

public class SessionMemberRequiredException extends RuntimeException {
    public SessionMemberRequiredException() {
        super("La sesión debe estar asociada a un socio.");
    }
}