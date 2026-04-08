package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.subscription;

import com.trainingapp.trainingapp.domain.enums.subscription.SubscriptionStatus;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.subscription.SubscriptionJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class SubscriptionJpaRepositoryTest {

    @Autowired
    private SubscriptionJpaRepository repository;

    @Test
    @DisplayName("Debería traer solo las suscripciones ACTIVE cuya endDate sea anterior a hoy")
    void findSubscriptionsToExpire_ShouldReturnOnlyOutdatedAndActive() {
        // Arrange: Guardamos 3 entidades diferentes en H2
        SubscriptionJpaEntity expiredActive = new SubscriptionJpaEntity();
        expiredActive.setMemberId(1L);
        expiredActive.setPlanId(1L);
        expiredActive.setPlanName("Mensual");
        expiredActive.setStatus(SubscriptionStatus.ACTIVE);
        expiredActive.setStartDate(LocalDate.now().minusDays(40));
        expiredActive.setEndDate(LocalDate.now().minusDays(10)); // VENCIDA y ACTIVA
        repository.save(expiredActive);

        SubscriptionJpaEntity validActive = new SubscriptionJpaEntity();
        validActive.setMemberId(2L);
        validActive.setPlanId(1L);
        validActive.setPlanName("Mensual");
        validActive.setStatus(SubscriptionStatus.ACTIVE);
        validActive.setStartDate(LocalDate.now().minusDays(10));
        validActive.setEndDate(LocalDate.now().plusDays(20)); // VIGENTE
        repository.save(validActive);

        SubscriptionJpaEntity expiredAlready = new SubscriptionJpaEntity();
        expiredAlready.setMemberId(3L);
        expiredAlready.setPlanId(1L);
        expiredAlready.setPlanName("Mensual");
        expiredAlready.setStatus(SubscriptionStatus.EXPIRED); // YA EXPIRADA
        expiredAlready.setStartDate(LocalDate.now().minusDays(40));
        expiredAlready.setEndDate(LocalDate.now().minusDays(10));
        repository.save(expiredAlready);

        List<SubscriptionJpaEntity> results =
                repository.findByStatusAndEndDateBefore(SubscriptionStatus.ACTIVE, LocalDate.now());

        assertEquals(1, results.size());
        assertEquals(expiredActive.getMemberId(), results.get(0).getMemberId());
    }
}