package com.trainingapp.trainingapp.application.useCase.membership;

import com.trainingapp.trainingapp.application.mapper.membershipPlan.MembershipPlanDTOMapper;
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
    private final MembershipPlanDTOMapper membershipPlanDTOMapper;

    public GetAllMembershipPlansByGymIdUseCase(MembershipPlanRepository planRepository, SecurityUtils securityUtils,
                                               MembershipPlanDTOMapper membershipPlanDTOMapper) {
        this.planRepository = planRepository;
        this.securityUtils = securityUtils;
        this.membershipPlanDTOMapper = membershipPlanDTOMapper;
    }

    public List<MembershipPlanResponse> execute(Long gymId) {
        securityUtils.validateSameGym(gymId);

        List<MembershipPlan> plans = planRepository.findByGymId(gymId);

        return  plans.stream()
                .map(membershipPlanDTOMapper::toResponse)
                .toList();
    }
}