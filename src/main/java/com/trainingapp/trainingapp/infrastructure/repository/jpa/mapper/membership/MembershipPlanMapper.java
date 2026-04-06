package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.membership;

import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.membership.MembershipPlanJpaEntity;
import org.springframework.stereotype.Component;


@Component
public class MembershipPlanMapper {

    public MembershipPlan toDomain(MembershipPlanJpaEntity entity) {
        if (entity == null) return null;

        return MembershipPlan.restore(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getDurationMonths(),
                entity.getGymId(),
                entity.isActive()
        );
    }

    public MembershipPlanJpaEntity toEntity(MembershipPlan domainEntity) {
        if (domainEntity == null) return null;

        MembershipPlanJpaEntity entity = new MembershipPlanJpaEntity();
        entity.setId(domainEntity.getId());
        entity.setName(domainEntity.getName());
        entity.setDescription(domainEntity.getDescription());
        entity.setPrice(domainEntity.getPrice());
        entity.setDurationMonths(domainEntity.getDurationMonths());
        entity.setGymId(domainEntity.getGymId());
        entity.setActive(domainEntity.isActive());

        return entity;
    }
}