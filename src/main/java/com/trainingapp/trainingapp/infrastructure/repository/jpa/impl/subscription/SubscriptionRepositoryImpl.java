package com.trainingapp.trainingapp.infrastructure.repository.jpa.impl.subscription;

import com.trainingapp.trainingapp.domain.entity.subscription.Subscription;
import com.trainingapp.trainingapp.domain.enums.subscription.SubscriptionStatus;
import com.trainingapp.trainingapp.domain.repository.subscription.SubscriptionRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.subscription.SubscriptionJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.subscription.SubscriptionMapper;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.subscription.SubscriptionJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

    private final SubscriptionJpaRepository jpaRepository;
    private final SubscriptionMapper mapper;

    public SubscriptionRepositoryImpl(SubscriptionJpaRepository jpaRepository, SubscriptionMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Subscription save(Subscription subscription) {
        SubscriptionJpaEntity entityToSave = mapper.toEntity(subscription);
        SubscriptionJpaEntity savedEntity = jpaRepository.save(entityToSave);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Subscription> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Subscription> findActiveByMemberId(Long memberId) {
        return jpaRepository.findActiveByMemberId(memberId, SubscriptionStatus.ACTIVE, LocalDate.now())
                .map(mapper::toDomain);
    }

    @Override
    public List<Subscription> findAllByMemberIdOrderByStartDateDesc(Long memberId) {
        return jpaRepository.findAllByMemberIdOrderByStartDateDesc(memberId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}