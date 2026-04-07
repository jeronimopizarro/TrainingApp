package com.trainingapp.trainingapp.domain.repository.tracker;

import com.trainingapp.trainingapp.domain.entity.tracker.TrainingSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TrainingSessionRepository {
    TrainingSession save(TrainingSession session);

    Optional<TrainingSession> findById(Long id);

    Optional<TrainingSession> findActiveSessionByMemberId(Long memberId);

    List<LocalDate> findTrainingDatesByMemberIdAndMonth(Long memberId, LocalDateTime startOfMonth,
                                                        LocalDateTime endOfMonth);

    List<TrainingSession> findZombieSessions(LocalDateTime threshold);

    Optional<TrainingSession> findLastSessionByMemberIdAndRoutineId(Long memberId, Long routineId);

    // Analítica Deportiva
    List<Long> findPerformedExerciseIdsByMemberId(Long memberId);

    List<TrainingSession> findSessionsByMemberAndExercise(Long memberId, Long exerciseId, LocalDateTime since);
}