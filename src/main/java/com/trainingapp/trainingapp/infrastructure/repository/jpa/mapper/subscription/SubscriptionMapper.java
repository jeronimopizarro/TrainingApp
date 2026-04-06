package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.subscription;

import com.trainingapp.trainingapp.domain.entity.subscription.Subscription;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.subscription.SubscriptionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionMapper {

    public SubscriptionJpaEntity toEntity(Subscription domain) {
        if (domain == null) return null;

        SubscriptionJpaEntity entity = new SubscriptionJpaEntity();
        entity.setId(domain.getId());
        entity.setStartDate(domain.getStartDate());
        entity.setEndDate(domain.getEndDate());
        entity.setStatus(domain.getStatus());
        entity.setMemberId(domain.getMemberId());
        entity.setPlanId(domain.getPlanId());
        entity.setPlanName(domain.getPlanName());

        return entity;
    }

    public Subscription toDomain(SubscriptionJpaEntity entity) {
        if (entity == null) return null;

        return Subscription.restore(
                entity.getId(),
                entity.getMemberId(),
                entity.getPlanId(),
                entity.getPlanName(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStatus()
        );
    }
}