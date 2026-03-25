package com.trainingapp.trainingapp.web.dto.sale;

import java.math.BigDecimal;

public record SaleDetailResponse(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}