package com.trainingapp.trainingapp.application.useCase.membership;

import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.domain.exception.membership.MembershipNotFoundException;
import com.trainingapp.trainingapp.domain.repository.membership.MembershipPlanRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DeleteMembershipPlanUseCase {

    private final MembershipPlanRepository planRepository;
    private final SecurityUtils securityUtils;

    public DeleteMembershipPlanUseCase(MembershipPlanRepository planRepository, SecurityUtils securityUtils) {
        this.planRepository = planRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public void execute(Long id) {
        MembershipPlan membershipPlan = findMemberShipOrThrow(id);
        securityUtils.validateSameGym(membershipPlan.getGymId());

        membershipPlan.deactivate();

        planRepository.save(membershipPlan);
    }

    private MembershipPlan findMemberShipOrThrow(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new MembershipNotFoundException(id));
    }
}