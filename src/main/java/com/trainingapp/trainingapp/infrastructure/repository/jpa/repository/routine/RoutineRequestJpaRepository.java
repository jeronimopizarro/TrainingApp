package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.routine;

import com.trainingapp.trainingapp.domain.enums.routine.RoutineRequestStatus;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.routine.RoutineRequestJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoutineRequestJpaRepository extends JpaRepository<RoutineRequestJpaEntity, Long> {

    boolean existsByMemberIdAndStatus(Long memberId, RoutineRequestStatus status);

    List<RoutineRequestJpaEntity> findByGymIdAndStatus(Long gymId, RoutineRequestStatus status);

    Optional<RoutineRequestJpaEntity> findFirstByMemberIdAndStatus(Long memberId,
                                                                   RoutineRequestStatus status);

    Optional<RoutineRequestJpaEntity> findFirstByMemberIdAndStatusAndAssignedTrainerId(
            Long memberId, RoutineRequestStatus status, Long assignedTrainerId);
}