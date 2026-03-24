package com.trainingapp.trainingapp.domain.entity.transaction;

import com.trainingapp.trainingapp.domain.enums.transaction.PaymentMethod;
import com.trainingapp.trainingapp.domain.enums.transaction.TransactionCategory;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class Transaction {

    private Long id;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private PaymentMethod paymentMethod;
    private TransactionCategory category;
    private String notes;

    private Long gymId;
    private Long registeredByAdminId;
    private Long subscriptionId;
    private Long saleId;

    public Transaction(Long id, BigDecimal amount, LocalDateTime transactionDate,
                       PaymentMethod paymentMethod,
                       TransactionCategory category, String notes, Long gymId,
                       Long registeredByAdminId,
                       Long subscriptionId, Long saleId) {
        validateTransaction(amount, paymentMethod, category, gymId, registeredByAdminId);

        this.id = id;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.paymentMethod = paymentMethod;
        this.category = category;
        this.notes = notes;
        this.gymId = gymId;
        this.registeredByAdminId = registeredByAdminId;
        this.subscriptionId = subscriptionId;
        this.saleId = saleId;
    }

    private void validateTransaction(BigDecimal amount, PaymentMethod paymentMethod,
                                     TransactionCategory category,
                                     Long gymId, Long registeredByAdminId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto de la transacción debe ser mayor a cero.");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("El método de pago es obligatorio.");
        }
        if (category == null) {
            throw new IllegalArgumentException("La categoría de la transacción es obligatoria.");
        }
        if (gymId == null) {
            throw new IllegalArgumentException(
                    "Toda transacción debe estar asociada a un gimnasio.");
        }
        if (registeredByAdminId == null) {
            throw new IllegalArgumentException(
                    "Toda transacción debe registrar el administrador que la cobró.");
        }
    }

    public static Transaction createNew(BigDecimal amount, PaymentMethod paymentMethod,
                                        TransactionCategory category,
                                        String notes, Long gymId, Long registeredByAdminId,
                                        Long subscriptionId, Long saleId) {

        if (category == TransactionCategory.MEMBERSHIP && subscriptionId == null) {
            throw new IllegalArgumentException(
                    "Una transacción de membresía debe tener una suscripción asociada.");
        }
        if (category == TransactionCategory.PRODUCT && saleId == null) {
            throw new IllegalArgumentException(
                    "Una transacción de kiosco debe tener una venta asociada.");
        }

        return new Transaction(null, amount, LocalDateTime.now(), paymentMethod, category, notes,
                gymId, registeredByAdminId, subscriptionId, saleId);
    }
}