package com.trainingapp.trainingapp.web.dto.subscription;

import com.trainingapp.trainingapp.domain.enums.subscription.SubscriptionStatus;

import java.time.LocalDate;

public record SubscriptionResponse(
        Long id,
        Long memberId,
        Long planId,
        String planName,
        LocalDate startDate,
        LocalDate endDate,
        SubscriptionStatus status,
        Integer remainingDays,
        boolean isActive
) {
}