package com.trainingapp.trainingapp.application.useCase.membership;

import com.trainingapp.trainingapp.application.mapper.membershipPlan.MembershipPlanDTOMapper;
import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.domain.exception.membership.DuplicateMembershipPlanNameException;
import com.trainingapp.trainingapp.domain.exception.membership.MembershipNotFoundException;
import com.trainingapp.trainingapp.domain.repository.membership.MembershipPlanRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.membership.MembershipPlanResponse;
import com.trainingapp.trainingapp.web.dto.membership.UpdateMembershipPlanRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UpdateMembershipPlanUseCase {

    private final MembershipPlanRepository planRepository;
    private final SecurityUtils securityUtils;
    private final MembershipPlanDTOMapper membershipPlanDTOMapper;

    public UpdateMembershipPlanUseCase(MembershipPlanRepository planRepository, SecurityUtils securityUtils,
                                       MembershipPlanDTOMapper membershipPlanDTOMapper) {
        this.planRepository = planRepository;
        this.securityUtils = securityUtils;
        this.membershipPlanDTOMapper = membershipPlanDTOMapper;
    }

    @Transactional
    public MembershipPlanResponse execute(Long id, UpdateMembershipPlanRequest request) {
        MembershipPlan plan = findMembershipPlanOrThrow(id);

        securityUtils.validateSameGym(plan.getGymId());
        validatePlanNameIsUniqueForUpdate(request.name(), plan.getGymId(), id);

        plan.update(request.name(), request.description(), request.price(), request.durationMonths());

        MembershipPlan updatedPlan = planRepository.save(plan);
        return membershipPlanDTOMapper.toResponse(updatedPlan);
    }

    private MembershipPlan findMembershipPlanOrThrow(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new MembershipNotFoundException(id));
    }

    private void validatePlanNameIsUniqueForUpdate(String name, Long gymId, Long currentId) {
        if (planRepository.existsByNameAndGymIdAndIdNot(name, gymId, currentId)) {
            throw new DuplicateMembershipPlanNameException(name);
        }
    }
}