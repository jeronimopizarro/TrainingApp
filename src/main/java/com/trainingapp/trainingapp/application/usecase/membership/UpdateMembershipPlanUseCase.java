package com.trainingapp.trainingapp.application.usecase.membership;

import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.domain.exception.membership.MembershipNotFoundException;
import com.trainingapp.trainingapp.domain.exception.user.MemberNotFoundException;
import com.trainingapp.trainingapp.domain.repository.membership.MembershipPlanRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.membership.MembershipPlanResponse;
import com.trainingapp.trainingapp.web.dto.membership.UpdateMembershipPlanRequest;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Service
public class UpdateMembershipPlanUseCase {

    private final MembershipPlanRepository planRepository;
    private final SecurityUtils securityUtils;

    public UpdateMembershipPlanUseCase(MembershipPlanRepository planRepository, SecurityUtils securityUtils) {
        this.planRepository = planRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public MembershipPlanResponse execute(Long id, UpdateMembershipPlanRequest request) {
        MembershipPlan plan = findMembershipPlanOrThrow(id);
        // Validamos que el Admin no modifique el plan otro gym.
        securityUtils.validateSameGym(plan.getGymId());

        plan.update(request.name(), request.description(), request.price(), request.durationDays());

        MembershipPlan updatedPlan = planRepository.save(plan);

        return buildResponseFromMembershipPlan(updatedPlan);
    }

    private MembershipPlan findMembershipPlanOrThrow(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new MembershipNotFoundException("Plan no encontrado"));
    }

    private MembershipPlanResponse buildResponseFromMembershipPlan(
            MembershipPlan updatedPlan) {
        return new MembershipPlanResponse(updatedPlan.getId(), updatedPlan.getName(),
                updatedPlan.getDescription(),
                updatedPlan.getPrice(), updatedPlan.getDurationDays(), updatedPlan.getGymId(),
                updatedPlan.isActive());
    }
}