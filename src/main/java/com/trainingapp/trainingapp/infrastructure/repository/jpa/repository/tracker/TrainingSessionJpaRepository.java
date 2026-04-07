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

    List<TrainingSessionJpaEntity> findByStatusAndStartTimeBefore(SessionStatus status,
                                                                  LocalDateTime thresholdTime);

    Optional<TrainingSessionJpaEntity> findFirstByMemberIdAndRoutineIdOrderByStartTimeDesc(
            Long memberId, Long routineId);

    @Query("SELECT ts FROM TrainingSessionJpaEntity ts " +
            "WHERE ts.memberId = :memberId " +
            "AND ts.startTime >= :startOfMonth " +
            "AND ts.startTime <= :endOfMonth " +
            "ORDER BY ts.startTime ASC")
    List<TrainingSessionJpaEntity> findSessionsByMemberIdAndMonth(@Param("memberId") Long memberId,
                                                                  @Param("startOfMonth") LocalDateTime startOfMonth,
                                                                  @Param("endOfMonth") LocalDateTime endOfMonth);

    /**
     * NIVEL 1: PANTALLA DE PROGRESO (RESUMEN)
     * Busca los IDs únicos de todos los ejercicios que el alumno realizó alguna vez.
     */
    @Query("SELECT DISTINCT s.exerciseId FROM TrainingSessionJpaEntity ts JOIN ts.sets s " +
            "WHERE ts.memberId = :memberId AND ts.status = 'COMPLETED'")
    List<Long> findDistinctExerciseIdsByMemberId(@Param("memberId") Long memberId);

    /**
     * NIVEL 2: PANTALLA DE DETALLE (GRÁFICO)
     * Busca todas las sesiones de entrenamiento de un alumno que contengan un ejercicio específico,
     * a partir de una fecha determinada (ej. los últimos 6 meses), ordenadas de más vieja a más nueva.
     */
    @Query("SELECT DISTINCT ts FROM TrainingSessionJpaEntity ts JOIN ts.sets s " +
            "WHERE ts.memberId = :memberId " +
            "AND s.exerciseId = :exerciseId " +
            "AND ts.status = 'COMPLETED' " +
            "AND ts.startTime >= :since " +
            "ORDER BY ts.startTime ASC")
    List<TrainingSessionJpaEntity> findSessionsByMemberAndExercise(
            @Param("memberId") Long memberId,
            @Param("exerciseId") Long exerciseId,
            @Param("since") LocalDateTime since);
}