package com.trainingapp.trainingapp.application.mapper.subscription;

import com.trainingapp.trainingapp.domain.entity.subscription.Subscription;
import com.trainingapp.trainingapp.web.dto.subscription.CreateSubscriptionRequest;
import com.trainingapp.trainingapp.web.dto.subscription.SubscriptionResponse;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionDTOMapper {

    public Subscription toDomain(CreateSubscriptionRequest request, String planName, Integer planDurationMonths) {
        if (request == null) return null;

        return Subscription.createNew(
                request.memberId(),
                request.planId(),
                planName,
                request.startDate(),
                planDurationMonths
        );
    }

    public SubscriptionResponse toResponse(Subscription subscription) {
        if (subscription == null) return null;

        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getMemberId(),
                subscription.getPlanId(),
                subscription.getPlanName(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.getStatus(),
                subscription.getRemainingDays(),
                subscription.isActive()
        );
    }
}