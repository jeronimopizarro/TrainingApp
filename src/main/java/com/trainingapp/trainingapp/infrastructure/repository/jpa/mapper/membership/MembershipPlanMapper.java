package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.membership;

import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.membership.MembershipPlanJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class MembershipPlanMapper {

    public MembershipPlanJpaEntity toEntity(MembershipPlan plan) {
        if (plan == null) return null;
        return new MembershipPlanJpaEntity(
                plan.getId(),
                plan.getName(),
                plan.getDescription(),
                plan.getPrice(),
                plan.getDurationMonths(),
                plan.getGymId(),
                plan.isActive()
        );
    }

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
}
