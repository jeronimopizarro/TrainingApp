package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.user;

import com.trainingapp.trainingapp.domain.enums.subscription.SubscriptionStatus;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.MemberJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, Long> {

    List<MemberJpaEntity> findByGymIdAndActiveTrue(Long gymId);

    @Query("SELECT m FROM MemberJpaEntity m WHERE m.gymId = :gymId AND m.active = true " +
            "AND EXISTS (SELECT s FROM SubscriptionJpaEntity s WHERE s.memberId = m.id " +
            "AND s.status = :status AND s.endDate >= :today)")
    List<MemberJpaEntity> findByGymIdAndSubscriptionStatus(
            @Param("gymId") Long gymId,
            @Param("status") SubscriptionStatus status,
            @Param("today") LocalDate today);

    @Query("SELECT m FROM MemberJpaEntity m WHERE m.gymId = :gymId AND m.active = true " +
            "AND NOT EXISTS (SELECT s FROM SubscriptionJpaEntity s WHERE s.memberId = m.id " +
            "AND s.status = 'ACTIVE' AND s.endDate >= :today)")
    List<MemberJpaEntity> findByGymIdAndSubscriptionInactive(
            @Param("gymId") Long gymId,
            @Param("today") LocalDate today);

    Optional<MemberJpaEntity> findByDni(String dni);
}