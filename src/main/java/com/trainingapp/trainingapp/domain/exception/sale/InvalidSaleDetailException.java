package com.trainingapp.trainingapp.domain.exception.sale;

public class InvalidSaleDetailException extends RuntimeException {
    public InvalidSaleDetailException(String message) {
        super(message);
    }
}
