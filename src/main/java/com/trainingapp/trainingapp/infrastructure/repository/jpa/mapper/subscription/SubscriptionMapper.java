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

        Subscription domain = new Subscription();
        domain.setId(entity.getId());
        domain.setPlanName(entity.getPlanName());
        domain.setStartDate(entity.getStartDate());
        domain.setEndDate(entity.getEndDate());
        domain.setStatus(entity.getStatus());
        domain.setMemberId(entity.getMemberId());
        domain.setPlanId(entity.getPlanId());

        return domain;
    }
}