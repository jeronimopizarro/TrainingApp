package com.trainingapp.trainingapp.application.useCase.membership;

import com.trainingapp.trainingapp.application.mapper.membershipPlan.MembershipPlanDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.domain.repository.membership.MembershipPlanRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.membership.CreateMembershipPlanRequest;
import com.trainingapp.trainingapp.web.dto.membership.MembershipPlanResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateMembershipPlanUseCase {

    private final MembershipPlanRepository planRepository;
    private final SecurityUtils securityUtils;
    private final MembershipPlanDTOMapper membershipPlanDTOMapper;
    private final GymValidator gymValidator;

    public CreateMembershipPlanUseCase(MembershipPlanRepository planRepository,
                                       SecurityUtils securityUtils,
                                       MembershipPlanDTOMapper membershipPlanDTOMapper,
                                       GymValidator gymValidator) {
        this.planRepository = planRepository;
        this.securityUtils = securityUtils;
        this.membershipPlanDTOMapper = membershipPlanDTOMapper;
        this.gymValidator = gymValidator;
    }

    @Transactional
    public MembershipPlanResponse execute(CreateMembershipPlanRequest request) {
        gymValidator.validateExists(request.gymId());
        securityUtils.validateSameGym(request.gymId());
        validatePlanNameIsUnique(request.name(), request.gymId());

        MembershipPlan plan = membershipPlanDTOMapper.toDomain(request);

        MembershipPlan savedPlan = planRepository.save(plan);

        return membershipPlanDTOMapper.toResponse(savedPlan);
    }

    private void validatePlanNameIsUnique(String name, Long gymId) {
        if (planRepository.existsByNameAndGymId(name, gymId)) {
            throw new IllegalArgumentException("Ya existe un plan activo con el nombre '" + name + "' en tu gimnasio.");
        }
    }
}