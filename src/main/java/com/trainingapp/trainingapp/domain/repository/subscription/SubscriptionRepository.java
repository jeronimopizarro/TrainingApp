package com.trainingapp.trainingapp.domain.repository.subscription;

import com.trainingapp.trainingapp.domain.entity.subscription.Subscription;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository {
    Subscription save(Subscription subscription);
    Optional<Subscription> findById(Long id);
    Optional<Subscription> findActiveByMemberId(Long memberId);
    List<Subscription> findAllByMemberIdOrderByStartDateDesc(Long memberId);
}