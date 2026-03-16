package com.trainingapp.trainingapp.application.usecase.membership;

import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.domain.exception.membership.DuplicateMembershipPlanNameException;
import com.trainingapp.trainingapp.domain.exception.membership.MembershipNotFoundException;
import com.trainingapp.trainingapp.domain.exception.user.MemberNotFoundException;
import com.trainingapp.trainingapp.domain.repository.membership.MembershipPlanRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.membership.MembershipPlanMapper;
import com.trainingapp.trainingapp.web.dto.membership.MembershipPlanResponse;
import com.trainingapp.trainingapp.web.dto.membership.UpdateMembershipPlanRequest;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Service
public class UpdateMembershipPlanUseCase {

    private final MembershipPlanRepository planRepository;
    private final SecurityUtils securityUtils;
    private final MembershipPlanMapper  membershipPlanMapper;

    public UpdateMembershipPlanUseCase(MembershipPlanRepository planRepository, SecurityUtils securityUtils,
                                       MembershipPlanMapper membershipPlanMapper) {
        this.planRepository = planRepository;
        this.securityUtils = securityUtils;
        this.membershipPlanMapper = membershipPlanMapper;
    }

    @Transactional
    public MembershipPlanResponse execute(Long id, UpdateMembershipPlanRequest request) {
        MembershipPlan plan = findMembershipPlanOrThrow(id);
        securityUtils.validateSameGym(plan.getGymId());
        validatePlanNameIsUniqueForUpdate(request.name(), plan.getGymId(), id);

        plan.update(request.name(), request.description(), request.price(), request.durationDays());

        MembershipPlan updatedPlan = planRepository.save(plan);
        return membershipPlanMapper.toResponse(updatedPlan);
    }

    private MembershipPlan findMembershipPlanOrThrow(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new MembershipNotFoundException("Plan no encontrado"));
    }

    private void validatePlanNameIsUniqueForUpdate(String name, Long gymId, Long currentId) {
        if (planRepository.existsByNameAndGymIdAndIdNot(name, gymId, currentId)) {
            throw new DuplicateMembershipPlanNameException("Ya existe otro plan activo con el nombre '" + name + "' en tu gimnasio.");
        }
    }
}