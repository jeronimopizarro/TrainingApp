package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.membership;

import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.membership.MembershipPlanJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class MembershipPlanMapper {

    public MembershipPlan toDomainEntity(MembershipPlanJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;

        MembershipPlan plan = new MembershipPlan(
                jpaEntity.getName(),
                jpaEntity.getDescription(),
                jpaEntity.getPrice(),
                jpaEntity.getDurationDays(),
                jpaEntity.getGymId()
        );
        plan.setId(jpaEntity.getId());
        plan.setActive(jpaEntity.getDeletedAt() == null); // Si no tiene fecha de borrado, está activo

        return plan;
    }

    public MembershipPlanJpaEntity toJpaEntity(MembershipPlan domainEntity) {
        if (domainEntity == null) return null;

        MembershipPlanJpaEntity jpaEntity = new MembershipPlanJpaEntity();
        jpaEntity.setId(domainEntity.getId());
        jpaEntity.setName(domainEntity.getName());
        jpaEntity.setDescription(domainEntity.getDescription());
        jpaEntity.setPrice(domainEntity.getPrice());
        jpaEntity.setDurationDays(domainEntity.getDurationDays());
        jpaEntity.setGymId(domainEntity.getGymId());

        return jpaEntity;
    }
}