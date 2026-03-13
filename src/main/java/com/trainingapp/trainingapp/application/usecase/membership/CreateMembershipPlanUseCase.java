package com.trainingapp.trainingapp.application.usecase.membership;

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

    public CreateMembershipPlanUseCase(MembershipPlanRepository planRepository,
                                       SecurityUtils securityUtils) {
        this.planRepository = planRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public MembershipPlanResponse execute(CreateMembershipPlanRequest request) {
        securityUtils.validateSameGym(request.gymId());

        MembershipPlan plan = buildPlanFromRequest(request);

        MembershipPlan savedPlan = planRepository.save(plan);

        return mapToResponse(savedPlan);
    }

    private MembershipPlan buildPlanFromRequest(CreateMembershipPlanRequest request) {
        return new MembershipPlan(
                request.name(),
                request.description(),
                request.price(),
                request.durationDays(),
                request.gymId()
        );
    }

    private MembershipPlanResponse mapToResponse(MembershipPlan plan) {
        return new MembershipPlanResponse(plan.getId(), plan.getName(), plan.getDescription(),
                plan.getPrice(), plan.getDurationDays(), plan.getGymId(), plan.isActive());
    }
}