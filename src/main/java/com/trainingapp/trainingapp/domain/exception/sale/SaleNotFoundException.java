package com.trainingapp.trainingapp.domain.exception.sale;

public class SaleNotFoundException extends RuntimeException {
    public SaleNotFoundException(String message) {
        super(message);
    }
}
