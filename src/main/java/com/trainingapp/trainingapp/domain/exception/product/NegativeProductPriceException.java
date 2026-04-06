package com.trainingapp.trainingapp.domain.exception.product;

public class NegativeProductPriceException extends RuntimeException {
    public NegativeProductPriceException() {
        super("El precio del producto no puede ser negativo ni nulo.");
    }
}
