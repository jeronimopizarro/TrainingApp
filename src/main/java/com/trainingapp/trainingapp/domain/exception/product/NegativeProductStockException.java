package com.trainingapp.trainingapp.domain.exception.product;

public class NegativeProductStockException extends RuntimeException {
    public NegativeProductStockException() {
        super("El stock del producto no puede ser negativo.");
    }
}
