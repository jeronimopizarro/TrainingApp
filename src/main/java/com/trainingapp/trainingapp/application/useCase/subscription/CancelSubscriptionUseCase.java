package com.trainingapp.trainingapp.application.useCase.subscription;

import com.trainingapp.trainingapp.application.mapper.subscription.SubscriptionDTOMapper;
import com.trainingapp.trainingapp.application.validator.MemberAccessValidator;
import com.trainingapp.trainingapp.domain.entity.subscription.Subscription;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.exception.subscription.SubscriptionNotFoundException;
import com.trainingapp.trainingapp.domain.exception.user.MemberNotFoundException;
import com.trainingapp.trainingapp.domain.repository.subscription.SubscriptionRepository;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.subscription.SubscriptionResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CancelSubscriptionUseCase {

    private final SubscriptionRepository subscriptionRepository;
    private final MemberRepository memberRepository;
    private final SecurityUtils securityUtils;
    private final SubscriptionDTOMapper subscriptionDTOMapper;
    private final MemberAccessValidator memberAccessValidator;

    public CancelSubscriptionUseCase(SubscriptionRepository subscriptionRepository,
                                     MemberRepository memberRepository,
                                     SecurityUtils securityUtils,
                                     SubscriptionDTOMapper subscriptionDTOMapper,
                                     MemberAccessValidator memberAccessValidator) {
        this.subscriptionRepository = subscriptionRepository;
        this.memberRepository = memberRepository;
        this.securityUtils = securityUtils;
        this.subscriptionDTOMapper = subscriptionDTOMapper;
        this.memberAccessValidator = memberAccessValidator;
    }

    @Transactional
    public SubscriptionResponse execute(Long subscriptionId) {
        Subscription subscription = findSubscriptionOrThrow(subscriptionId);

        memberAccessValidator.findMemberAndValidateAccess(subscription.getMemberId());
        validateCanBeCancelled(subscription);

        subscription.cancel();

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        return subscriptionDTOMapper.toResponse(savedSubscription);
    }

    private Subscription findSubscriptionOrThrow(Long subscriptionId) {
        return subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException("La suscripción no existe."));
    }

    private void validateCanBeCancelled(Subscription subscription) {
        if (subscription.isCancelled()) {
            throw new IllegalStateException("La suscripción ya está cancelada.");
        }
        if (subscription.isExpired()) {
            throw new IllegalStateException("No se puede cancelar una suscripción que ya ha vencido.");
        }
    }
}
