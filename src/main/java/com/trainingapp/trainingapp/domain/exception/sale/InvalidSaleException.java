package com.trainingapp.trainingapp.domain.exception.sale;

public class InvalidSaleException extends RuntimeException {
    public InvalidSaleException(String message) {
        super(message);
    }
}
