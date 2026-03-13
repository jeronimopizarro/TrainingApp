package com.trainingapp.trainingapp.domain.exception.membership;

public class MembershipNotFoundException extends RuntimeException {
    public MembershipNotFoundException(String message) {
        super(message);
    }
}