package com.trainingapp.trainingapp.application.useCase.membership;

import com.trainingapp.trainingapp.application.mapper.membershipPlan.MembershipPlanDTOMapper;
import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.domain.exception.membership.DuplicateMembershipPlanNameException;
import com.trainingapp.trainingapp.domain.repository.membership.MembershipPlanRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.membership.CreateMembershipPlanRequest;
import com.trainingapp.trainingapp.web.dto.membership.MembershipPlanResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateMembershipPlanUseCase {

    private final MembershipPlanRepository repository;
    private final MembershipPlanDTOMapper mapper;
    private final SecurityUtils securityUtils;

    public CreateMembershipPlanUseCase(MembershipPlanRepository repository,
                                       MembershipPlanDTOMapper mapper,
                                       SecurityUtils securityUtils) {
        this.repository = repository;
        this.mapper = mapper;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public MembershipPlanResponse execute(CreateMembershipPlanRequest request) {
        securityUtils.validateSameGym(request.gymId());

        if (repository.existsByNameAndGymId(request.name(), request.gymId())) {
            throw new DuplicateMembershipPlanNameException(request.name());
        }

        MembershipPlan plan = MembershipPlan.createNew(
                request.name(),
                request.description(),
                request.price(),
                request.durationMonths(),
                request.gymId()
        );

        return mapper.toResponse(repository.save(plan));
    }
}
