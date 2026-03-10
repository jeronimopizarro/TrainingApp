package com.trainingapp.trainingapp.domain.exception.user;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}