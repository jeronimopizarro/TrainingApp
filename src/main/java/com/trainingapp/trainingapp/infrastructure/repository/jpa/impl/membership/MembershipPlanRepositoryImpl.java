package com.trainingapp.trainingapp.infrastructure.repository.jpa.impl.membership;

import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.domain.repository.membership.MembershipPlanRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.membership.MembershipPlanJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.membership.MembershipPlanMapper;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.membership.MembershipPlanJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MembershipPlanRepositoryImpl implements MembershipPlanRepository {

    private final MembershipPlanJpaRepository jpaRepository;
    private final MembershipPlanMapper mapper;

    public MembershipPlanRepositoryImpl(MembershipPlanJpaRepository jpaRepository, MembershipPlanMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public MembershipPlan save(MembershipPlan plan) {
        MembershipPlanJpaEntity jpaEntity = mapper.toJpaEntity(plan);
        MembershipPlanJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        return mapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<MembershipPlan> findById(Long id) {
        return jpaRepository.findByIdAndActiveTrue(id).map(mapper::toDomainEntity);
    }

    @Override
    public List<MembershipPlan> findByGymId(Long gymId) {
        return jpaRepository.findByGymIdAndActiveTrue(gymId)
                .stream().map(mapper::toDomainEntity).toList();
    }
}
