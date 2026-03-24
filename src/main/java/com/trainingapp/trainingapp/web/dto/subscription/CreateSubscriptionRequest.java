package com.trainingapp.trainingapp.web.dto.subscription;

import com.trainingapp.trainingapp.domain.enums.transaction.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateSubscriptionRequest(
        @NotNull(message = "El ID del socio es obligatorio")
        Long memberId,

        @NotNull(message = "El ID del plan es obligatorio")
        Long planId,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate startDate,

        @NotNull(message = "El método de pago es obligatorio")
        PaymentMethod paymentMethod
) {
}