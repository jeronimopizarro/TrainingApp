package com.trainingapp.trainingapp.domain.exception.user;

public class AdminNotFoundException extends RuntimeException {
    public AdminNotFoundException(String message) {
        super(message);
    }
}