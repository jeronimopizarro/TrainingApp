package com.trainingapp.trainingapp.domain.exception.membership;

public class InvalidMembershipDurationException extends RuntimeException {
    public InvalidMembershipDurationException() {
        super("La duración de la membresía debe ser de al menos 1 mes.");
    }
}
