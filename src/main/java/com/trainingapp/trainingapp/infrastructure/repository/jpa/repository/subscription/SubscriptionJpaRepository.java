package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.subscription;

import com.trainingapp.trainingapp.domain.enums.subscription.SubscriptionStatus;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.subscription.SubscriptionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubscriptionJpaRepository extends JpaRepository<SubscriptionJpaEntity, Long> {

    List<SubscriptionJpaEntity> findAllByMemberIdOrderByStartDateDesc(Long memberId);

    @Query("SELECT s FROM SubscriptionJpaEntity s WHERE s.memberId = :memberId " +
            "AND s.status = :status AND s.endDate >= :currentDate")
    Optional<SubscriptionJpaEntity> findActiveByMemberId(
            @Param("memberId") Long memberId,
            @Param("status") SubscriptionStatus status,
            @Param("currentDate") LocalDate currentDate
    );

    List<SubscriptionJpaEntity> findByStatusAndEndDateBefore(SubscriptionStatus status, LocalDate date);
}