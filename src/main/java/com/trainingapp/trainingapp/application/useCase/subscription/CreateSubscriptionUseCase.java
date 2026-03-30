package com.trainingapp.trainingapp.application.useCase.subscription;

import com.trainingapp.trainingapp.application.mapper.subscription.SubscriptionDTOMapper;
import com.trainingapp.trainingapp.application.useCase.transaction.RegisterTransactionCommand;
import com.trainingapp.trainingapp.application.useCase.transaction.RegisterTransactionUseCase;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.application.validator.MemberAccessValidator;
import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.domain.entity.subscription.Subscription;
import com.trainingapp.trainingapp.domain.enums.transaction.TransactionCategory;
import com.trainingapp.trainingapp.domain.exception.membership.InactiveMembershipPlanException;
import com.trainingapp.trainingapp.domain.exception.membership.MembershipNotFoundException;
import com.trainingapp.trainingapp.domain.exception.subscription.ActiveSubscriptionAlreadyExistsException;
import com.trainingapp.trainingapp.domain.exception.subscription.InvalidSubscriptionStartDateException;
import com.trainingapp.trainingapp.domain.repository.membership.MembershipPlanRepository;
import com.trainingapp.trainingapp.domain.repository.subscription.SubscriptionRepository;
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
    private final MembershipPlanRepository planRepository;
    private final SubscriptionDTOMapper subscriptionDTOMapper;
    private final SecurityUtils securityUtils;
    private final GymValidator gymValidator;
    private final MemberAccessValidator memberAccessValidator;
    private final RegisterTransactionUseCase registerTransactionUseCase;

    public CreateSubscriptionUseCase(SubscriptionRepository subscriptionRepository,
                                     MembershipPlanRepository planRepository,
                                     SubscriptionDTOMapper subscriptionDTOMapper,
                                     SecurityUtils securityUtils,
                                     GymValidator gymValidator,
                                     MemberAccessValidator memberAccessValidator,
                                     RegisterTransactionUseCase registerTransactionUseCase) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.subscriptionDTOMapper = subscriptionDTOMapper;
        this.securityUtils = securityUtils;
        this.gymValidator = gymValidator;
        this.memberAccessValidator = memberAccessValidator;
        this.registerTransactionUseCase = registerTransactionUseCase;
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
                request, plan.getName(), plan.getDurationMonths());

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        RegisterTransactionCommand
                command = buildTransactionCommand(plan, request, savedSubscription);
        registerTransactionUseCase.execute(command);

        return subscriptionDTOMapper.toResponse(savedSubscription);
    }

    private MembershipPlan findPlanAndValidateAccess(Long planId) {
        MembershipPlan plan = planRepository.findById(planId)
                .orElseThrow(
                        () -> new MembershipNotFoundException(planId));

        securityUtils.validateSameGym(plan.getGymId());

        if (!plan.isActive()) {
            throw new InactiveMembershipPlanException();
        }
        return plan;
    }

    private void validateSubscriptionRules(CreateSubscriptionRequest request) {
        if (request.startDate().isBefore(LocalDate.now())) {
            throw new InvalidSubscriptionStartDateException();
        }

        Optional<Subscription> active =
                subscriptionRepository.findActiveByMemberId(request.memberId());
        if (active.isPresent()) {
            throw new ActiveSubscriptionAlreadyExistsException(active.get().getEndDate());
        }
    }

    private RegisterTransactionCommand buildTransactionCommand(MembershipPlan plan,
                                                               CreateSubscriptionRequest request,
                                                               Subscription savedSubscription) {
        return new RegisterTransactionCommand(
                plan.getPrice(),
                request.paymentMethod(),
                TransactionCategory.MEMBERSHIP,
                "Pago de cuota: " + plan.getName(),
                plan.getGymId(),
                securityUtils.getCurrentUser().getId(), // Sacamos el Admin/Cajero
                savedSubscription.getId(),
                null // No es una venta de kiosco
        );
    }
}