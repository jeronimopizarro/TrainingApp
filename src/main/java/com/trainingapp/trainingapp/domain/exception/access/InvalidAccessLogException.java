package com.trainingapp.trainingapp.domain.exception.access;

public class InvalidAccessLogException extends RuntimeException {
    public InvalidAccessLogException(String message) {
        super(message);
    }
}