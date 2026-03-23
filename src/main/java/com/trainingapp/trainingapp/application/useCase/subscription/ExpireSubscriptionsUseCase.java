package com.trainingapp.trainingapp.application.useCase.subscription;

import com.trainingapp.trainingapp.domain.entity.subscription.Subscription;
import com.trainingapp.trainingapp.domain.enums.subscription.SubscriptionStatus;
import com.trainingapp.trainingapp.domain.repository.subscription.SubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class ExpireSubscriptionsUseCase {

    private final SubscriptionRepository subscriptionRepository;

    public ExpireSubscriptionsUseCase(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public void execute() {
        LocalDate today = LocalDate.now();

        List<Subscription> expiredSubscriptions = subscriptionRepository.findByStatusAndEndDateBefore(
                SubscriptionStatus.ACTIVE, today);

        if (expiredSubscriptions.isEmpty()) {
            log.info("Job de expiración: No se encontraron suscripciones para expirar hoy.");
            return;
        }

        expiredSubscriptions.forEach(Subscription::markAsExpired);

        subscriptionRepository.saveAll(expiredSubscriptions);
        log.info("Job de expiración: Se han expirado {} suscripciones exitosamente.", expiredSubscriptions.size());
    }
}