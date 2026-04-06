package com.trainingapp.trainingapp.infrastructure.repository.jpa.impl.tracker;

import com.trainingapp.trainingapp.domain.entity.tracker.TrainingSession;
import com.trainingapp.trainingapp.domain.enums.tracker.SessionStatus;
import com.trainingapp.trainingapp.domain.repository.tracker.TrainingSessionRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.tracker.TrainingSessionJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.tracker.TrainingSessionMapper;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.tracker.TrainingSessionJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

    @Override
    public List<LocalDate> findTrainingDatesByMemberIdAndMonth(Long memberId, LocalDateTime startOfMonth, LocalDateTime endOfMonth) {
        // Usamos la query JPA que armaste en el paso anterior
        return jpaRepository.findSessionsByMemberIdAndMonth(memberId, startOfMonth, endOfMonth)
                .stream()
                .map(session -> session.getStartTime().toLocalDate()) // Extraemos la fecha
                .distinct() // Filtramos repetidos
                .toList();
    }

    @Override
    public List<TrainingSession> findZombieSessions(LocalDateTime threshold) {
        return jpaRepository.findByStatusAndStartTimeBefore(SessionStatus.IN_PROGRESS, threshold)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<TrainingSession> findLastSessionByMemberIdAndRoutineId(Long memberId, Long routineId) {
        return jpaRepository.findFirstByMemberIdAndRoutineIdOrderByStartTimeDesc(memberId, routineId)
                .map(mapper::toDomain);
    }
}