package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository;

import com.trainingapp.trainingapp.domain.Enum.RoutineStatus;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.RoutineJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RoutineJpaRepository extends JpaRepository<RoutineJpaEntity, Long> {
    List<RoutineJpaEntity> findAllByMemberId(Long memberId);
    boolean existsByMemberIdAndStatus(Long memberId, RoutineStatus status);
    Optional<RoutineJpaEntity> findByMemberIdAndStatus(Long memberId, RoutineStatus status);
}