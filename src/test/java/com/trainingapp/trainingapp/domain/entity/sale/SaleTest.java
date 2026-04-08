package com.trainingapp.trainingapp.domain.entity.sale;

import com.trainingapp.trainingapp.domain.enums.transaction.PaymentMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SaleTest {

    @Test
    @DisplayName("Debería calcular el total automáticamente sumando los subtotales de los detalles")
    void shouldCalculateTotalAmount_BasedOnDetails() {
        SaleDetail agua = SaleDetail.createNew(1L, 2, new BigDecimal("2.50")); // Subtotal: 5.00
        SaleDetail barra = SaleDetail.createNew(2L, 1, new BigDecimal("3.00")); // Subtotal: 3.00

        Sale sale = Sale.createNew(PaymentMethod.CASH, 10L, 2L, 100L, List.of(agua, barra));

        assertEquals(new BigDecimal("8.00"), sale.getTotalAmount());
    }
}