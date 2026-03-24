package com.trainingapp.trainingapp.application.useCase.transaction;

import com.trainingapp.trainingapp.domain.enums.transaction.PaymentMethod;
import com.trainingapp.trainingapp.domain.enums.transaction.TransactionCategory;

import java.math.BigDecimal;

public record RegisterTransactionCommand(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        TransactionCategory category,
        String notes,
        Long gymId,
        Long registeredByAdminId,
        Long subscriptionId,
        Long saleId
) {
}