package com.trainingapp.trainingapp.domain.repository.routine;

import com.trainingapp.trainingapp.domain.Enum.routine.RoutineStatus;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;

import java.util.List;
import java.util.Optional;

public interface RoutineRepository {

    Routine save(Routine routine);
    Optional<Routine> findById(Long id);
    List<Routine> findAllByMemberId(Long memberId);
    Boolean existsByMemberIdAndStatus(Long memberId, RoutineStatus status);
    Optional<Routine> findByMemberIdAndStatus(Long memberId, RoutineStatus status);
    void delete(Routine routine);
    List<Routine> findAllByTrainerId(Long trainerId);
}
