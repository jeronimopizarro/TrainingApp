package com.trainingapp.trainingapp.application.useCase.subscription;

import com.trainingapp.trainingapp.application.mapper.subscription.SubscriptionDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.application.validator.MemberAccessValidator;
import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.domain.entity.subscription.Subscription;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.exception.membership.MembershipNotFoundException;
import com.trainingapp.trainingapp.domain.exception.membership.MembershipPlanAccessDeniedException;
import com.trainingapp.trainingapp.domain.exception.subscription.ActiveSubscriptionAlreadyExistsException;
import com.trainingapp.trainingapp.domain.exception.user.MemberNotFoundException;
import com.trainingapp.trainingapp.domain.repository.membership.MembershipPlanRepository;
import com.trainingapp.trainingapp.domain.repository.subscription.SubscriptionRepository;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.subscription.CreateSubscriptionRequest;
import com.trainingapp.trainingapp.web.dto.subscription.SubscriptionResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CreateSubscriptionUseCase {

    private final SubscriptionRepository subscriptionRepository;
    private final MemberRepository memberRepository;
    private final MembershipPlanRepository planRepository;
    private final SubscriptionDTOMapper subscriptionDTOMapper;
    private final SecurityUtils securityUtils;
    private final GymValidator gymValidator;
    private final MemberAccessValidator memberAccessValidator;

    public CreateSubscriptionUseCase(SubscriptionRepository subscriptionRepository,
                                     MemberRepository memberRepository,
                                     MembershipPlanRepository planRepository,
                                     SubscriptionDTOMapper subscriptionDTOMapper,
                                     SecurityUtils securityUtils,
                                     GymValidator gymValidator,
                                     MemberAccessValidator memberAccessValidator) {
        this.subscriptionRepository = subscriptionRepository;
        this.memberRepository = memberRepository;
        this.planRepository = planRepository;
        this.subscriptionDTOMapper = subscriptionDTOMapper;
        this.securityUtils = securityUtils;
        this.gymValidator = gymValidator;
        this.memberAccessValidator = memberAccessValidator;
    }

    @Transactional
    public SubscriptionResponse execute(CreateSubscriptionRequest request) {
        Long currentGymId = securityUtils.getCurrentUserGymId();

        if (currentGymId != null) {
            gymValidator.validateExists(currentGymId);
        }

        memberAccessValidator.findMemberAndValidateAccess(request.memberId());

        MembershipPlan plan = findPlanAndValidateAccess(request.planId());

        validateSubscriptionRules(request);

        Subscription subscription = subscriptionDTOMapper.toDomain(
                request, plan.getName(), plan.getDurationDays());

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        return subscriptionDTOMapper.toResponse(savedSubscription);
    }

    private MembershipPlan findPlanAndValidateAccess(Long planId) {
        MembershipPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new MembershipNotFoundException("El plan seleccionado no existe."));

        // Magia de tu proyecto: Esto maneja la lógica de Super Admin automáticamente
        securityUtils.validateSameGym(plan.getGymId());

        if (!plan.isActive()) {
            throw new IllegalArgumentException("El plan seleccionado está inactivo.");
        }
        return plan;
    }

    private void validateSubscriptionRules(CreateSubscriptionRequest request) {
        if (request.startDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser anterior a hoy.");
        }

        Optional<Subscription> active = subscriptionRepository.findActiveByMemberId(request.memberId());
        if (active.isPresent()) {
            throw new ActiveSubscriptionAlreadyExistsException(
                    "El socio ya posee una suscripción activa que vence el: " + active.get().getEndDate()
            );
        }
    }
}