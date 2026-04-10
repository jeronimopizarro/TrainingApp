package com.trainingapp.trainingapp.domain.entity.transaction;

import com.trainingapp.trainingapp.domain.enums.transaction.PaymentMethod;
import com.trainingapp.trainingapp.domain.enums.transaction.TransactionCategory;
import com.trainingapp.trainingapp.domain.exception.transaction.InvalidTransactionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    @DisplayName("Debería crear una transacción válida de membresía")
    void shouldCreateValidMembershipTransaction() {
        Transaction transaction = Transaction.createNew(
                new BigDecimal("5000.00"), PaymentMethod.CASH, TransactionCategory.MEMBERSHIP,
                "Pago mensual", 10L, 1L, 100L, null
        );

        assertNotNull(transaction);
        assertNull(transaction.getId());
        assertEquals(new BigDecimal("5000.00"), transaction.getAmount());
        assertEquals(PaymentMethod.CASH, transaction.getPaymentMethod());
        assertEquals(TransactionCategory.MEMBERSHIP, transaction.getCategory());
        assertEquals(10L, transaction.getGymId());
        assertEquals(1L, transaction.getRegisteredByAdminId());
        assertEquals(100L, transaction.getSubscriptionId());
        assertNull(transaction.getSaleId());
        assertNotNull(transaction.getTransactionDate());
    }

    @Test
    @DisplayName("Debería crear una transacción válida de producto")
    void shouldCreateValidProductTransaction() {
        Transaction transaction = Transaction.createNew(
                new BigDecimal("1500.00"), PaymentMethod.CARD, TransactionCategory.PRODUCT,
                "Compra de agua", 10L, 1L, null, 200L
        );

        assertNotNull(transaction);
        assertEquals(TransactionCategory.PRODUCT, transaction.getCategory());
        assertEquals(200L, transaction.getSaleId());
        assertNull(transaction.getSubscriptionId());
    }

    @Test
    @DisplayName("Debería lanzar error si el monto es cero o negativo")
    void shouldThrowExceptionWhenAmountIsInvalid() {
        assertThrows(InvalidTransactionException.class, () -> Transaction.createNew(
                BigDecimal.ZERO, PaymentMethod.CASH, TransactionCategory.MEMBERSHIP, "", 10L, 1L, 100L, null
        ), "El monto de la transacción debe ser mayor a cero.");

        assertThrows(InvalidTransactionException.class, () -> Transaction.createNew(
                new BigDecimal("-500.00"), PaymentMethod.CASH, TransactionCategory.MEMBERSHIP, "", 10L, 1L, 100L, null
        ));
    }

    @Test
    @DisplayName("Debería lanzar error si faltan campos obligatorios (PaymentMethod, Category, GymId, AdminId)")
    void shouldThrowExceptionWhenRequiredFieldsAreMissing() {
        assertThrows(InvalidTransactionException.class, () -> Transaction.createNew(
                new BigDecimal("1000"), null, TransactionCategory.MEMBERSHIP, "", 10L, 1L, 100L, null
        ));

        assertThrows(InvalidTransactionException.class, () -> Transaction.createNew(
                new BigDecimal("1000"), PaymentMethod.CASH, null, "", 10L, 1L, 100L, null
        ));

        assertThrows(InvalidTransactionException.class, () -> Transaction.createNew(
                new BigDecimal("1000"), PaymentMethod.CASH, TransactionCategory.MEMBERSHIP, "", null, 1L, 100L, null
        ));

        assertThrows(InvalidTransactionException.class, () -> Transaction.createNew(
                new BigDecimal("1000"), PaymentMethod.CASH, TransactionCategory.MEMBERSHIP, "", 10L, null, 100L, null
        ));
    }

    @Test
    @DisplayName("Debería lanzar error si es de MEMBERSHIP pero no tiene SubscriptionId")
    void shouldThrowExceptionWhenMembershipLacksSubscriptionId() {
        assertThrows(InvalidTransactionException.class, () -> Transaction.createNew(
                new BigDecimal("5000"), PaymentMethod.CASH, TransactionCategory.MEMBERSHIP, "", 10L, 1L, null, null
        ), "Una transacción de membresía debe tener una suscripción asociada.");
    }

    @Test
    @DisplayName("Debería lanzar error si es de PRODUCT pero no tiene SaleId")
    void shouldThrowExceptionWhenProductLacksSaleId() {
        assertThrows(InvalidTransactionException.class, () -> Transaction.createNew(
                new BigDecimal("1500"), PaymentMethod.CASH, TransactionCategory.PRODUCT, "", 10L, 1L, null, null
        ), "Una transacción de kiosco debe tener una venta asociada.");
    }

    @Test
    @DisplayName("Debería restaurar una transacción correctamente")
    void shouldRestoreTransaction() {
        LocalDateTime date = LocalDateTime.now().minusDays(1);
        Transaction transaction = Transaction.restore(
                1L, new BigDecimal("2000.00"), date, PaymentMethod.CASH, TransactionCategory.MEMBERSHIP,
                "Restaurada", 10L, 1L, 100L, null
        );

        assertNotNull(transaction);
        assertEquals(1L, transaction.getId());
        assertEquals(date, transaction.getTransactionDate());
    }
}