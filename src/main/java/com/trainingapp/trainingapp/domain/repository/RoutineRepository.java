package com.trainingapp.trainingapp.domain.repository;

import com.trainingapp.trainingapp.domain.Enum.RoutineStatus;
import com.trainingapp.trainingapp.domain.entity.Routine;

import java.util.List;
import java.util.Optional;

public interface RoutineRepository {

    Routine save(Routine routine);
    Optional<Routine> findById(Long id);
    List<Routine> findAllByMemberId(Long memberId);
    Boolean existsByMemberIdAndStatus(Long memberId, RoutineStatus status);
    Optional<Routine> findByMemberIdAndStatus(Long memberId, RoutineStatus status);
}
