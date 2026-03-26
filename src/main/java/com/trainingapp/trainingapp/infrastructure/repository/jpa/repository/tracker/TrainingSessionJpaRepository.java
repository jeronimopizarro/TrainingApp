package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.tracker;

import com.trainingapp.trainingapp.domain.enums.tracker.SessionStatus;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.tracker.TrainingSessionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainingSessionJpaRepository extends JpaRepository<TrainingSessionJpaEntity, Long> {
    // Evitamos que un alumno inicie dos entrenamientos al mismo tiempo
    Optional<TrainingSessionJpaEntity> findByMemberIdAndStatus(Long memberId, SessionStatus status);
}