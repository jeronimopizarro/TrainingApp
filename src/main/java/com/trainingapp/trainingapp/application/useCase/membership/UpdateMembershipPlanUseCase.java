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

    private final MembershipPlanRepository repository;
    private final MembershipPlanDTOMapper mapper;
    private final SecurityUtils securityUtils;

    public UpdateMembershipPlanUseCase(MembershipPlanRepository repository,
                                       MembershipPlanDTOMapper mapper,
                                       SecurityUtils securityUtils) {
        this.repository = repository;
        this.mapper = mapper;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public MembershipPlanResponse execute(Long id, UpdateMembershipPlanRequest request) {
        MembershipPlan plan = repository.findById(id)
                .orElseThrow(() -> new MembershipNotFoundException(id));

        securityUtils.validateSameGym(plan.getGymId());

        if (repository.existsByNameAndGymIdAndIdNot(request.name(), plan.getGymId(), id)) {
            throw new DuplicateMembershipPlanNameException(request.name());
        }

        plan.updateDetails(
                request.name(),
                request.description(),
                request.price(),
                request.durationMonths()
        );

        return mapper.toResponse(repository.save(plan));
    }
}
