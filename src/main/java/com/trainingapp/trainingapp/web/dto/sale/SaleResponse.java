package com.trainingapp.trainingapp.web.dto.sale;

import com.trainingapp.trainingapp.domain.enums.transaction.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SaleResponse(
        Long id,
        LocalDateTime saleDate,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        Long gymId,
        Long registeredByAdminId,
        Long memberId,
        List<SaleDetailResponse> details
) {
}