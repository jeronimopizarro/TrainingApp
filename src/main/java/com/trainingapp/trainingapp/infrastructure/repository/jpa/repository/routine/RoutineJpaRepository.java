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

    List<RoutineJpaEntity> findAllByMemberIdAndActiveTrue(Long memberId);

    boolean existsByMemberIdAndStatusAndActiveTrue(Long memberId, RoutineStatus status);

    Optional<RoutineJpaEntity> findByMemberIdAndStatusAndActiveTrue(Long memberId, RoutineStatus status);

    List<RoutineJpaEntity> findAllByTrainerIdAndActive(Long trainerId);

    @Query("SELECT new com.trainingapp.trainingapp.domain.entity.routine.RoutineSummary(r.id, r.name, r.status, r.memberId) " +
            "FROM RoutineJpaEntity r WHERE r.memberId = :memberId")
    List<RoutineSummary> findAllSummariesByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT new com.trainingapp.trainingapp.domain.entity.routine.RoutineSummary(r.id, r.name, r.status, r.memberId) " +
            "FROM RoutineJpaEntity r WHERE r.trainerId = :trainerId")
    List<RoutineSummary> findAllSummariesByTrainerId(@Param("trainerId") Long trainerId);
}