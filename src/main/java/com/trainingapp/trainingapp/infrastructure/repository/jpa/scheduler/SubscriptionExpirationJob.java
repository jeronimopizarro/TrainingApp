package com.trainingapp.trainingapp.infrastructure.repository.jpa.scheduler;

import com.trainingapp.trainingapp.application.useCase.subscription.ExpireSubscriptionsUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionExpirationJob {

    private final ExpireSubscriptionsUseCase expireSubscriptionsUseCase;

    public SubscriptionExpirationJob(ExpireSubscriptionsUseCase expireSubscriptionsUseCase) {
        this.expireSubscriptionsUseCase = expireSubscriptionsUseCase;
    }
    /**
     * Se ejecuta todos los días a las 00:01 AM.
     */
     @Scheduled(cron = "0 1 0 * * ?")
    public void runExpirationJob() {
        expireSubscriptionsUseCase.execute();
    }
}