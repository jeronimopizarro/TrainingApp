package com.trainingapp.trainingapp.domain.exception.membership;

public class NegativeMembershipPriceException extends RuntimeException {
    public NegativeMembershipPriceException() {
        super("El precio de la membresía no puede ser negativo ni nulo.");
    }
}
