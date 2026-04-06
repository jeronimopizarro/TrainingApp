package com.trainingapp.trainingapp.domain.exception.product;

public class ProductNameRequiredException extends RuntimeException {
    public ProductNameRequiredException() {
        super("El nombre del producto es obligatorio y no puede estar vacío.");
    }
}
