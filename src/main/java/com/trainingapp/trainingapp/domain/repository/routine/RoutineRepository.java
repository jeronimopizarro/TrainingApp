package com.trainingapp.trainingapp.domain.repository.routine;

import com.trainingapp.trainingapp.domain.entity.routine.RoutineSummary;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import java.util.List;
import java.util.Optional;

public interface RoutineRepository {

    Routine save(Routine routine);
    Optional<Routine> findById(Long id);
    Boolean existsByMemberIdAndStatus(Long memberId, RoutineStatus status);
    Optional<Routine> findByMemberIdAndStatus(Long memberId, RoutineStatus status);
    void delete(Routine routine);
    List<RoutineSummary> findAllSummariesByMemberId(Long memberId);
    List<RoutineSummary> findAllSummariesByTrainerId(Long trainerId);
}
