package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.tracker;

import com.trainingapp.trainingapp.domain.enums.tracker.SessionStatus;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.tracker.TrainingSessionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingSessionJpaRepository
        extends JpaRepository<TrainingSessionJpaEntity, Long> {
    // Evitamos que un alumno inicie dos entrenamientos al mismo tiempo
    Optional<TrainingSessionJpaEntity> findByMemberIdAndStatus(Long memberId, SessionStatus status);
    
    @Query("SELECT ts FROM TrainingSessionJpaEntity ts " +
            "WHERE ts.memberId = :memberId " +
            "AND ts.startTime >= :startOfMonth " +
            "AND ts.startTime <= :endOfMonth " +
            "ORDER BY ts.startTime ASC")
    List<TrainingSessionJpaEntity> findSessionsByMemberIdAndMonth(@Param("memberId") Long memberId,
                                                                  @Param("startOfMonth") LocalDateTime startOfMonth,
                                                                  @Param("endOfMonth") LocalDateTime endOfMonth);
}