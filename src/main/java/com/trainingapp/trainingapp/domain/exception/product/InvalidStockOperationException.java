package com.trainingapp.trainingapp.domain.exception.product;

public class InvalidStockOperationException extends RuntimeException {
    public InvalidStockOperationException() {
        super("La cantidad de stock a agregar o descontar debe ser mayor a cero.");
    }
}
