package com.trainingapp.trainingapp.application.mapper.membershipPlan;

import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.web.dto.membership.CreateMembershipPlanRequest;
import com.trainingapp.trainingapp.web.dto.membership.MembershipPlanResponse;
import org.springframework.stereotype.Component;

@Component
public class MembershipPlanDTOMapper {

    public MembershipPlan toDomain(CreateMembershipPlanRequest request) {
        if (request == null) return null;
        return MembershipPlan.createNew(
                request.name(),
                request.description(),
                request.price(),
                request.durationMonths(),
                request.gymId()
        );
    }

    public MembershipPlanResponse toResponse(MembershipPlan plan) {
        if (plan == null) return null;
        return new MembershipPlanResponse(
                plan.getId(),
                plan.getName(),
                plan.getDescription(),
                plan.getPrice(),
                plan.getDurationMonths(),
                plan.getGymId(),
                plan.isActive()
        );
    }
}
