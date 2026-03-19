package com.trainingapp.trainingapp.application.useCase.subscription;

import com.trainingapp.trainingapp.application.mapper.subscription.SubscriptionDTOMapper;
import com.trainingapp.trainingapp.application.validator.MemberAccessValidator;
import com.trainingapp.trainingapp.domain.entity.subscription.Subscription;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.exception.user.MemberNotFoundException;
import com.trainingapp.trainingapp.domain.repository.subscription.SubscriptionRepository;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.subscription.SubscriptionResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetAllSubscriptionsByMemberUseCase {

    private final SubscriptionRepository subscriptionRepository;
    private final MemberRepository memberRepository;
    private final SecurityUtils securityUtils;
    private final SubscriptionDTOMapper subscriptionDTOMapper;
    private final MemberAccessValidator memberAccessValidator;

    public GetAllSubscriptionsByMemberUseCase(SubscriptionRepository subscriptionRepository,
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
    public List<SubscriptionResponse> execute(Long memberId) {
        memberAccessValidator.findMemberAndValidateAccess(memberId);

        List<Subscription> subscriptions = subscriptionRepository.findAllByMemberIdOrderByStartDateDesc(memberId);

        return subscriptions.stream()
                .map(subscriptionDTOMapper::toResponse)
                .collect(Collectors.toList());
    }
}