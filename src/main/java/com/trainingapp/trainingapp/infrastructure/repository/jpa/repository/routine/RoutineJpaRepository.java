package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.routine;

import com.trainingapp.trainingapp.domain.entity.routine.RoutineSummary;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.routine.RoutineJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoutineJpaRepository extends JpaRepository<RoutineJpaEntity, Long> {

    boolean existsByMemberIdAndStatusAndActiveTrue(Long memberId, RoutineStatus status);

    Optional<RoutineJpaEntity> findByMemberIdAndStatusAndActiveTrue(Long memberId,
                                                                    RoutineStatus status);

    @Query("SELECT new com.trainingapp.trainingapp.domain.entity.routine.RoutineSummary(r.id, r.name, r.status, r.memberId, CONCAT(u.firstName, ' ', u.lastName)) " +
            "FROM RoutineJpaEntity r JOIN UserJpaEntity u ON r.memberId = u.id WHERE r.memberId = :memberId AND r.active = true")
    List<RoutineSummary> findAllSummariesByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT new com.trainingapp.trainingapp.domain.entity.routine.RoutineSummary(r.id, r.name, r.status, r.memberId, CONCAT(u.firstName, ' ', u.lastName)) " +
            "FROM RoutineJpaEntity r JOIN UserJpaEntity u ON r.memberId = u.id WHERE r.trainerId = :trainerId AND r.active = true")
    List<RoutineSummary> findAllSummariesByTrainerId(@Param("trainerId") Long trainerId);

    @Query("SELECT new com.trainingapp.trainingapp.domain.entity.routine.RoutineSummary(r.id, r.name, r.status, r.memberId, 'SISTEMA') " +
            "FROM RoutineJpaEntity r WHERE r.gymId = :gymId AND r.isBase = true AND r.active = true")
    List<RoutineSummary> findAllBaseRoutinesByGymId(@Param("gymId") Long gymId);
    }