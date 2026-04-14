package com.trainingapp.trainingapp.web.dto.transaction;

import com.trainingapp.trainingapp.domain.enums.transaction.PaymentMethod;
import com.trainingapp.trainingapp.domain.enums.transaction.TransactionCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        BigDecimal amount,
        LocalDateTime transactionDate,
        PaymentMethod paymentMethod,
        TransactionCategory category,
        String notes,
        Long gymId,
        Long registeredByAdminId,
        Long subscriptionId,
        Long saleId
) {}
