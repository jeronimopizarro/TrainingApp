package com.trainingapp.trainingapp.domain.exception.user;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(Long memberId) {
        super("No se encontró el socio con ID: " + memberId);
    }
}