package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.membership;

import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.membership.MembershipPlanJpaEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MembershipPlanMapper {

    public MembershipPlan toDomain(MembershipPlanJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;

        return new MembershipPlan(
                jpaEntity.getId(),
                jpaEntity.getName(),
                jpaEntity.getDescription(),
                jpaEntity.getPrice(),
                jpaEntity.getDurationMonths(),
                jpaEntity.getGymId(),
                jpaEntity.isActive()
        );
    }

    public MembershipPlanJpaEntity toEntity(MembershipPlan domainEntity) {
        if (domainEntity == null) return null;

        MembershipPlanJpaEntity jpaEntity = new MembershipPlanJpaEntity();
        jpaEntity.setId(domainEntity.getId());
        jpaEntity.setName(domainEntity.getName());
        jpaEntity.setDescription(domainEntity.getDescription());
        jpaEntity.setPrice(domainEntity.getPrice());
        jpaEntity.setDurationMonths(domainEntity.getDurationMonths());
        jpaEntity.setGymId(domainEntity.getGymId());
        jpaEntity.setActive(domainEntity.isActive());

        if (!domainEntity.isActive()) jpaEntity.setDeletedAt(LocalDateTime.now());

        return jpaEntity;
    }
}