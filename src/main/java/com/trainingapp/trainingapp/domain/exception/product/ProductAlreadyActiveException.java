package com.trainingapp.trainingapp.domain.exception.product;

public class ProductAlreadyActiveException extends RuntimeException {
    public ProductAlreadyActiveException() {
        super("El producto ya se encuentra activo.");
    }
}
