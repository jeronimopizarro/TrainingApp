package com.trainingapp.trainingapp.application.useCase.tracker;

import com.trainingapp.trainingapp.domain.entity.tracker.TrainingSession;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.tracker.TrainingSessionRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.tracker.MemberProgressSummaryResponse;
import com.trainingapp.trainingapp.web.dto.tracker.MemberProgressSummaryResponse.ExerciseSummaryDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GetMemberProgressSummaryUseCase {

    private final SecurityUtils securityUtils;
    private final TrainingSessionRepository trainingSessionRepository;
    private final ExerciseRepository exerciseRepository;

    public GetMemberProgressSummaryUseCase(SecurityUtils securityUtils,
                                           TrainingSessionRepository trainingSessionRepository,
                                           ExerciseRepository exerciseRepository) {
        this.securityUtils = securityUtils;
        this.trainingSessionRepository = trainingSessionRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @Transactional(readOnly = true)
    public MemberProgressSummaryResponse execute(Long targetMemberId) {
        Long memberId = (targetMemberId != null) ? targetMemberId : securityUtils.getCurrentUser().getId();

        // Si un staff está consultando a un socio, validamos que sean del mismo gym
        if (targetMemberId != null) {
            securityUtils.validateMemberAccess(targetMemberId);
        }

        // 1. Buscamos los IDs de todos los ejercicios que este alumno entrenó alguna vez
        List<Long> performedExerciseIds = trainingSessionRepository.findPerformedExerciseIdsByMemberId(memberId);

        List<ExerciseSummaryDTO> summaryList = new ArrayList<>();

        // 2. Para cada ejercicio, calculamos su récord
        for (Long exerciseId : performedExerciseIds) {
            exerciseRepository.findById(exerciseId).ifPresent(exercise -> {
                BigDecimal latestPR = calculateLatestPR(memberId, exerciseId);
                summaryList.add(new ExerciseSummaryDTO(exerciseId, exercise.getName(), latestPR, exercise.getImageUrl()));
            });
        }

        return new MemberProgressSummaryResponse(summaryList);
    }

    private BigDecimal calculateLatestPR(Long memberId, Long exerciseId) {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<TrainingSession> recentSessions = trainingSessionRepository
                .findSessionsByMemberAndExercise(memberId, exerciseId, threeMonthsAgo);

        return recentSessions.stream()
                // Delegamos el cálculo a la Entidad de Dominio
                .map(session -> session.calculateAverageE1RMForExercise(exerciseId))
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }
}