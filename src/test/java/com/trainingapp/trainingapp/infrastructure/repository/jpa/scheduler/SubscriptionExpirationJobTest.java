package com.trainingapp.trainingapp.infrastructure.repository.jpa.scheduler;

import com.trainingapp.trainingapp.application.useCase.subscription.ExpireSubscriptionsUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubscriptionExpirationJobTest {

    @Mock
    private ExpireSubscriptionsUseCase expireSubscriptionsUseCase;

    @InjectMocks
    private SubscriptionExpirationJob subscriptionExpirationJob;

    @Test
    @DisplayName("Debería ejecutar el caso de uso de expiración de suscripciones")
    void shouldExecuteExpireSubscriptionsUseCase() {
        subscriptionExpirationJob.runExpirationJob();

        verify(expireSubscriptionsUseCase, times(1)).execute();
    }
}