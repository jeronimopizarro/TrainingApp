package com.trainingapp.trainingapp.domain.exception.product;

public class ProductAlreadyInactiveException extends RuntimeException {
    public ProductAlreadyInactiveException() {
        super("El producto ya se encuentra inactivo.");
    }
}
