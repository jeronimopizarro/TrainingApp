package com.trainingapp.trainingapp.domain.entity.subscription;

import com.trainingapp.trainingapp.domain.enums.subscription.SubscriptionStatus;
import com.trainingapp.trainingapp.domain.exception.subscription.SubscriptionAlreadyExpiredException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionTest {

    @Nested
    @DisplayName("Cálculo de Fechas y Creación")
    class CreationAndDates {

        @Test
        @DisplayName("Debería calcular la fecha de fin automáticamente basada en los meses de duración")
        void shouldCalculateEndDate_WhenCreatingNew() {
            LocalDate startDate = LocalDate.of(2026, 1, 1);
            // Compramos 3 meses de suscripción
            Subscription subscription = Subscription.createNew(100L, 1L, "Plan Trimestral", startDate, 3);

            assertEquals(LocalDate.of(2026, 4, 1), subscription.getEndDate());
            assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus());
        }
    }

    @Nested
    @DisplayName("Expiración y Cancelación")
    class Lifecycle {

        @Test
        @DisplayName("Debería cambiar el estado a EXPIRED solo si estaba ACTIVE")
        void shouldMarkAsExpired_WhenActive() {
            Subscription subscription = Subscription.restore(
                    1L, 100L, 1L, "Mensual", LocalDate.now().minusDays(30), LocalDate.now().minusDays(1), SubscriptionStatus.ACTIVE
            );

            subscription.markAsExpired();

            assertEquals(SubscriptionStatus.EXPIRED, subscription.getStatus());
        }

        @Test
        @DisplayName("Debería lanzar error al cancelar si ya estaba expirada")
        void shouldThrowException_WhenCancellingAnExpiredSubscription() {
            Subscription subscription = Subscription.restore(
                    1L, 100L, 1L, "Mensual", LocalDate.now().minusDays(30), LocalDate.now().minusDays(1), SubscriptionStatus.EXPIRED
            );

            assertThrows(SubscriptionAlreadyExpiredException.class, subscription::cancel);
        }
    }
}