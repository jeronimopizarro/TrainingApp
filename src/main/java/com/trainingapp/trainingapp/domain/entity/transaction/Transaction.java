package com.trainingapp.trainingapp.domain.entity.transaction;

import com.trainingapp.trainingapp.domain.enums.transaction.PaymentMethod;
import com.trainingapp.trainingapp.domain.enums.transaction.TransactionCategory;
import com.trainingapp.trainingapp.domain.exception.transaction.InvalidTransactionException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class Transaction {

    private final Long id;
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
        validate();
    }

    private void validate() {
        if (this.amount == null || this.amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("El monto de la transacción debe ser mayor a cero.");
        }
        if (this.paymentMethod == null) {
            throw new InvalidTransactionException("El método de pago es obligatorio.");
        }
        if (this.category == null) {
            throw new InvalidTransactionException("La categoría de la transacción es obligatoria.");
        }
        if (this.gymId == null) {
            throw new InvalidTransactionException("Toda transacción debe estar asociada a un gimnasio.");
        }
        if (this.registeredByAdminId == null) {
            throw new InvalidTransactionException("Toda transacción debe registrar el administrador que la cobró.");
        }

        if (this.category == TransactionCategory.MEMBERSHIP && this.subscriptionId == null) {
            throw new InvalidTransactionException("Una transacción de membresía debe tener una suscripción asociada.");
        }
        if (this.category == TransactionCategory.PRODUCT && this.saleId == null) {
            throw new InvalidTransactionException("Una transacción de kiosco debe tener una venta asociada.");
        }
    }

    public static Transaction createNew(BigDecimal amount, PaymentMethod paymentMethod,
                                        TransactionCategory category, String notes,
                                        Long gymId, Long registeredByAdminId,
                                        Long subscriptionId, Long saleId) {
        return new Transaction(null, amount, LocalDateTime.now(), paymentMethod, category, notes,
                gymId, registeredByAdminId, subscriptionId, saleId);
    }

    public static Transaction restore(Long id, BigDecimal amount, LocalDateTime transactionDate,
                                      PaymentMethod paymentMethod, TransactionCategory category,
                                      String notes, Long gymId, Long registeredByAdminId,
                                      Long subscriptionId, Long saleId) {
        return new Transaction(id, amount, transactionDate, paymentMethod, category, notes,
                gymId, registeredByAdminId, subscriptionId, saleId);
    }
}