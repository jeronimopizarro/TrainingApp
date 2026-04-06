package com.trainingapp.trainingapp.domain.entity.subscription;

import com.trainingapp.trainingapp.domain.enums.subscription.SubscriptionStatus;
import com.trainingapp.trainingapp.domain.exception.subscription.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@NoArgsConstructor
public class Subscription {

    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private SubscriptionStatus status;
    private Long memberId;
    private Long planId;
    private String planName;

    public Subscription(Long id, Long memberId, Long planId, String planName, LocalDate startDate, LocalDate endDate, SubscriptionStatus status) {
        this.id = id;
        this.memberId = memberId;
        this.planId = planId;
        this.planName = planName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        validate();
    }

    private void validate() {
        if (this.memberId == null) throw new SubscriptionMemberRequiredException();
        if (this.planId == null) throw new SubscriptionPlanRequiredException();
        if (this.startDate == null) throw new SubscriptionStartDateRequiredException();
    }

    public static Subscription createNew(Long memberId, Long planId, String planName,
                                         LocalDate startDate, Integer planDurationMonths) {
        if (planDurationMonths == null || planDurationMonths <= 0) {
            throw new InvalidSubscriptionDurationException();
        }
        LocalDate calculatedEndDate = startDate.plusMonths(planDurationMonths);

        return new Subscription(null, memberId, planId, planName, startDate,
                calculatedEndDate, SubscriptionStatus.ACTIVE);
    }

    public static Subscription restore(Long id, Long memberId, Long planId, String planName,
                                       LocalDate startDate, LocalDate endDate, SubscriptionStatus status) {
        return new Subscription(id, memberId, planId, planName, startDate, endDate, status);
    }

    public void cancel() {
        if (this.isCancelled()) {
            throw new IllegalStateException("La suscripción ya se encuentra cancelada.");
        }
        if (this.isExpired()) {
            throw new SubscriptionAlreadyExpiredException();
        }
        this.status = SubscriptionStatus.CANCELLED;
        this.endDate = LocalDate.now();
    }

    public void markAsExpired() {
        if (this.status == SubscriptionStatus.ACTIVE) {
            this.status = SubscriptionStatus.EXPIRED;
        }
    }

    public boolean isActive() {
        return this.status == SubscriptionStatus.ACTIVE && !LocalDate.now().isAfter(this.endDate);
    }

    public boolean isExpired() {
        return this.status == SubscriptionStatus.EXPIRED || LocalDate.now().isAfter(this.endDate);
    }

    public boolean isCancelled() {
        return this.status == SubscriptionStatus.CANCELLED;
    }

    public Integer getRemainingDays() {
        if (!isActive()) {
            return 0;
        }
        long days = ChronoUnit.DAYS.between(LocalDate.now(), this.endDate);
        return days > 0 ? (int) days : 0;
    }
}