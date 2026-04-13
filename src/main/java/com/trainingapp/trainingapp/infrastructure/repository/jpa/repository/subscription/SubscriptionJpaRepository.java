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

    List<SubscriptionJpaEntity> findByStatusAndEndDateBefore(SubscriptionStatus status,
                                                             LocalDate date);

    @Query("SELECT COUNT(s) FROM SubscriptionJpaEntity s WHERE s.status = 'ACTIVE' " +
            "AND s.memberId IN (SELECT m.id FROM MemberJpaEntity m WHERE m.gymId = :gymId)")
    long countActiveMembersByGymId(@Param("gymId") Long gymId);

    @Query("SELECT COUNT(s) FROM SubscriptionJpaEntity s WHERE s.status = 'ACTIVE' " +
            "AND s.startDate >= :startDate AND s.startDate <= :endDate " +
            "AND s.memberId IN (SELECT m.id FROM MemberJpaEntity m WHERE m.gymId = :gymId)")
    long countNewMembersByGymIdAndDateRange(@Param("gymId") Long gymId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(DISTINCT s.memberId) FROM SubscriptionJpaEntity s " +
            "WHERE s.status = 'EXPIRED' " +
            "AND s.endDate >= :startDate AND s.endDate <= :endDate " +
            "AND s.memberId IN (SELECT m.id FROM MemberJpaEntity m WHERE m.gymId = :gymId) " +
            "AND s.memberId NOT IN (SELECT s2.memberId FROM SubscriptionJpaEntity s2 WHERE s2.status = 'ACTIVE')")
    long countChurnedMembersByGymIdAndDateRange(@Param("gymId") Long gymId,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    @Query("SELECT new com.trainingapp.trainingapp.web.dto.dashboard.AdminDashboardResponse$ExpiringMembershipDTO(m.id, m.firstName, m.lastName, s.endDate) " +
            "FROM SubscriptionJpaEntity s, MemberJpaEntity m " +
            "WHERE s.memberId = m.id AND m.gymId = :gymId " +
            "AND s.status = 'ACTIVE' " +
            "AND s.endDate >= :today AND s.endDate <= :limitDate " +
            "ORDER BY s.endDate ASC")
    List<com.trainingapp.trainingapp.web.dto.dashboard.AdminDashboardResponse.ExpiringMembershipDTO> findExpiringMemberships(
            @Param("gymId") Long gymId,
            @Param("today") LocalDate today,
            @Param("limitDate") LocalDate limitDate);
}