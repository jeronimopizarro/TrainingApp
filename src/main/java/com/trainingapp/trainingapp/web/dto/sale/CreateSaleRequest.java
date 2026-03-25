package com.trainingapp.trainingapp.web.dto.sale;

import com.trainingapp.trainingapp.domain.enums.transaction.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateSaleRequest(
        // memberId puede ser null (venta a un no-socio)
        Long memberId,

        @NotNull(message = "El método de pago es obligatorio")
        PaymentMethod paymentMethod,

        @NotEmpty(message = "La venta debe tener al menos un producto")
        @Valid // Para que valide cada renglón por separado
        List<SaleDetailRequest> details
) {
}