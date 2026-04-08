package com.trainingapp.trainingapp.application.useCase.subscription;

import com.trainingapp.trainingapp.domain.entity.subscription.Subscription;
import com.trainingapp.trainingapp.domain.enums.subscription.SubscriptionStatus;
import com.trainingapp.trainingapp.domain.repository.subscription.SubscriptionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpireSubscriptionsUseCaseTest {

    @Mock
    private SubscriptionRepository repository;

    @InjectMocks
    private ExpireSubscriptionsUseCase useCase;

    @Test
    @DisplayName("Debería encontrar suscripciones vencidas, marcarlas como EXPIRED y guardarlas")
    void shouldExpireOutdatedSubscriptions() {
        Subscription sub1 =
                Subscription.restore(1L, 100L, 1L, "Mensual", LocalDate.now().minusDays(40),
                        LocalDate.now().minusDays(10), SubscriptionStatus.ACTIVE);
        Subscription sub2 =
                Subscription.restore(2L, 101L, 1L, "Mensual", LocalDate.now().minusDays(35),
                        LocalDate.now().minusDays(5), SubscriptionStatus.ACTIVE);

        when(repository.findByStatusAndEndDateBefore(eq(SubscriptionStatus.ACTIVE),
                any(LocalDate.class)))
                .thenReturn(List.of(sub1, sub2));

        useCase.execute();

        assertEquals(SubscriptionStatus.EXPIRED, sub1.getStatus());
        assertEquals(SubscriptionStatus.EXPIRED, sub2.getStatus());
        verify(repository).saveAll(anyList());
    }
}