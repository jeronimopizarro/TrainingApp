package com.trainingapp.trainingapp.domain.exception.user;

public class MemberAccessDeniedException extends RuntimeException {
    public MemberAccessDeniedException(String message) {
        super(message);
    }
}