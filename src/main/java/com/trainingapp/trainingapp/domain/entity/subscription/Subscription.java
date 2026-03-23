package com.trainingapp.trainingapp.domain.entity.subscription;

import com.trainingapp.trainingapp.domain.enums.subscription.SubscriptionStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    public Subscription(Long memberId, Long planId, String planName, LocalDate startDate, Integer planDurationMonths) {
        validateSubscriptionData(memberId, planId, startDate, planDurationMonths);

        this.memberId = memberId;
        this.planId = planId;
        this.planName = planName;
        this.startDate = startDate;
        this.endDate = startDate.plusMonths(planDurationMonths);
        this.status = SubscriptionStatus.ACTIVE;
    }

    private void validateSubscriptionData(Long memberId, Long planId, LocalDate startDate, Integer planDurationDays) {
        if (memberId == null)
            throw new IllegalArgumentException("La suscripción debe tener un socio asociado.");
        if (planId == null)
            throw new IllegalArgumentException("La suscripción debe tener un plan asociado.");
        if (startDate == null)
            throw new IllegalArgumentException("La fecha de inicio no puede ser nula.");
        if (planDurationDays == null || planDurationDays <= 0) {
            throw new IllegalArgumentException("La duración del plan debe ser mayor a cero.");
        }
    }

    public void cancel() {
        if (this.isCancelled()) {
            throw new IllegalStateException("La suscripción ya se encuentra cancelada.");
        }
        this.status = SubscriptionStatus.CANCELLED;
        this.endDate = LocalDate.now();
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    // ... dentro de la clase Subscription
    public Integer getRemainingDays() {
        if (!isActive()) {
            return 0;
        }
        long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), this.endDate);
        return days > 0 ? (int) days : 0;
    }

    public void markAsExpired() {
        if (this.status == SubscriptionStatus.ACTIVE) {
            this.status = SubscriptionStatus.EXPIRED;
        }
    }
}