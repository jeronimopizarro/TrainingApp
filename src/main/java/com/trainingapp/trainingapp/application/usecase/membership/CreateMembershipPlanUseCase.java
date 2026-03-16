package com.trainingapp.trainingapp.application.usecase.membership;

import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.domain.repository.membership.MembershipPlanRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.membership.MembershipPlanMapper;
import com.trainingapp.trainingapp.web.dto.membership.CreateMembershipPlanRequest;
import com.trainingapp.trainingapp.web.dto.membership.MembershipPlanResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateMembershipPlanUseCase {

    private final MembershipPlanRepository planRepository;
    private final SecurityUtils securityUtils;
    private final MembershipPlanMapper membershipPlanMapper;

    public CreateMembershipPlanUseCase(MembershipPlanRepository planRepository,
                                       SecurityUtils securityUtils,
                                       MembershipPlanMapper membershipPlanMapper) {
        this.planRepository = planRepository;
        this.securityUtils = securityUtils;
        this.membershipPlanMapper = membershipPlanMapper;
    }

    @Transactional
    public MembershipPlanResponse execute(CreateMembershipPlanRequest request) {
        securityUtils.validateSameGym(request.gymId());
        validatePlanNameIsUnique(request.name(), request.gymId());

        MembershipPlan plan = membershipPlanMapper.toDomain(request);
        MembershipPlan savedPlan = planRepository.save(plan);
        return membershipPlanMapper.toResponse(savedPlan);
    }

    private void validatePlanNameIsUnique(String name, Long gymId) {
        if (planRepository.existsByNameAndGymId(name, gymId)) {
            throw new IllegalArgumentException("Ya existe un plan activo con el nombre '" + name + "' en tu gimnasio.");
        }
    }
}