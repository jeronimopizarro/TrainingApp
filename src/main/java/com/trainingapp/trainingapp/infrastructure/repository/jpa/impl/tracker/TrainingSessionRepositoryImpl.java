package com.trainingapp.trainingapp.infrastructure.repository.jpa.impl.tracker;

import com.trainingapp.trainingapp.domain.entity.tracker.TrainingSession;
import com.trainingapp.trainingapp.domain.enums.tracker.SessionStatus;
import com.trainingapp.trainingapp.domain.repository.tracker.TrainingSessionRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.tracker.TrainingSessionJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.tracker.TrainingSessionMapper;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.tracker.TrainingSessionJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TrainingSessionRepositoryImpl implements TrainingSessionRepository {

    private final TrainingSessionJpaRepository jpaRepository;
    private final TrainingSessionMapper mapper;

    public TrainingSessionRepositoryImpl(TrainingSessionJpaRepository jpaRepository, TrainingSessionMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public TrainingSession save(TrainingSession session) {
        TrainingSessionJpaEntity entity = mapper.toEntity(session);
        TrainingSessionJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<TrainingSession> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<TrainingSession> findActiveSessionByMemberId(Long memberId) {
        return jpaRepository.findByMemberIdAndStatus(memberId, SessionStatus.IN_PROGRESS)
                .map(mapper::toDomain);
    }
}