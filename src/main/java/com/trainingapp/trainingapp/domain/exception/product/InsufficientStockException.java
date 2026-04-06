package com.trainingapp.trainingapp.domain.exception.product;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String productName, Integer currentStock) {
        super("Stock insuficiente para el producto: '" + productName + "'. Stock actual: " + currentStock);
    }
}
