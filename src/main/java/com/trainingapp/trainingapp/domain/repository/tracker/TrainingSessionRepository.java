package com.trainingapp.trainingapp.domain.repository.tracker;

import com.trainingapp.trainingapp.domain.entity.tracker.TrainingSession;

import java.util.Optional;

public interface TrainingSessionRepository {
    TrainingSession save(TrainingSession session);

    Optional<TrainingSession> findById(Long id);

    Optional<TrainingSession> findActiveSessionByMemberId(Long memberId);
}