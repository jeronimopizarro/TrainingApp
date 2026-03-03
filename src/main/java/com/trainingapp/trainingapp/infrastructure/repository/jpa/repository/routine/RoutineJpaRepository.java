package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.routine;

import com.trainingapp.trainingapp.domain.Enum.routine.RoutineStatus;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.routine.RoutineJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RoutineJpaRepository extends JpaRepository<RoutineJpaEntity, Long> {
    List<RoutineJpaEntity> findAllByMemberId(Long memberId);
    boolean existsByMemberIdAndStatus(Long memberId, RoutineStatus status);
    Optional<RoutineJpaEntity> findByMemberIdAndStatus(Long memberId, RoutineStatus status);
    List<RoutineJpaEntity> findAllByTrainerId(Long trainerId);
}