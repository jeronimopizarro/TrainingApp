package com.trainingapp.trainingapp.application.usecase.membership;

import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.domain.repository.membership.MembershipPlanRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.membership.MembershipPlanResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllMembershipPlansByGymIdUseCase {

    private final MembershipPlanRepository planRepository;
    private final SecurityUtils securityUtils;

    public GetAllMembershipPlansByGymIdUseCase(MembershipPlanRepository planRepository, SecurityUtils securityUtils) {
        this.planRepository = planRepository;
        this.securityUtils = securityUtils;
    }

    public List<MembershipPlanResponse> execute(Long gymId) {
        securityUtils.validateSameGym(gymId);

        List<MembershipPlan> plans = planRepository.findByGymId(gymId);

        return mapToResponseList(plans);
    }

    private List<MembershipPlanResponse> mapToResponseList(List<MembershipPlan> plans) {
        return plans.stream()
                .map(this::buildResponseFromPlan)
                .toList();
    }

    private MembershipPlanResponse buildResponseFromPlan(MembershipPlan plan) {
        return new MembershipPlanResponse(
                plan.getId(),
                plan.getName(),
                plan.getDescription(),
                plan.getPrice(),
                plan.getDurationDays(),
                plan.getGymId(),
                plan.isActive()
        );
    }
}