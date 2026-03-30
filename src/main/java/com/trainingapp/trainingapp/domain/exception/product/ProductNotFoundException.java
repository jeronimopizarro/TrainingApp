package com.trainingapp.trainingapp.domain.exception.product;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("El producto con ID " + id + " no existe.");
    }
}