package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.transaction;

import com.trainingapp.trainingapp.domain.enums.transaction.PaymentMethod;
import com.trainingapp.trainingapp.domain.enums.transaction.TransactionCategory;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.transaction.TransactionJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class TransactionJpaRepositoryTest {

    @Autowired
    private TransactionJpaRepository repository;

    @Test
    @DisplayName("Debería sumar correctamente los ingresos de un gimnasio en un rango de fechas")
    void calculateRevenueByGymIdAndDateRange_ShouldSumCorrectly() {
        Long gymId = 10L;
        Long adminId = 1L;
        // Guardamos 3 transacciones en H2
        TransactionJpaEntity t1 = new TransactionJpaEntity();
        t1.setAmount(new BigDecimal("150.00"));
        t1.setTransactionDate(LocalDateTime.now().minusDays(5));
        t1.setPaymentMethod(PaymentMethod.CASH);
        t1.setCategory(TransactionCategory.PRODUCT);
        t1.setGymId(gymId);
        t1.setRegisteredByAdminId(adminId); // <-- Campo obligatorio agregado
        repository.save(t1);

        TransactionJpaEntity t2 = new TransactionJpaEntity();
        t2.setAmount(new BigDecimal("50.00"));
        t2.setTransactionDate(LocalDateTime.now().minusDays(2));
        t2.setPaymentMethod(PaymentMethod.CARD);
        t2.setCategory(TransactionCategory.MEMBERSHIP);
        t2.setGymId(gymId);
        t2.setRegisteredByAdminId(adminId);
        repository.save(t2);

        TransactionJpaEntity t3 = new TransactionJpaEntity();
        t3.setAmount(new BigDecimal("500.00"));
        t3.setTransactionDate(LocalDateTime.now().minusDays(2));
        t3.setPaymentMethod(PaymentMethod.CASH);
        t3.setCategory(TransactionCategory.PRODUCT);
        t3.setGymId(99L); // Gym diferente
        t3.setRegisteredByAdminId(adminId);
        repository.save(t3);

        // Calculamos la ganancia del gimnasio de los últimos 10 días
        BigDecimal totalRevenue = repository.sumRevenueByDateRange(
                gymId,
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().plusDays(1)
        );

        // 150 + 50 = 200 (El de 500 se ignora porque es otro gym)
        assertEquals(new BigDecimal("200.00"), totalRevenue);
    }
}