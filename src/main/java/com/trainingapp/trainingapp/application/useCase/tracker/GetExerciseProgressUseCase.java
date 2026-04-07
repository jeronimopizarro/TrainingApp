package com.trainingapp.trainingapp.application.useCase.tracker;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.tracker.TrainingSession;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseNotFoundException;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.tracker.TrainingSessionRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.tracker.ExerciseProgressResponse;
import com.trainingapp.trainingapp.web.dto.tracker.ExerciseProgressResponse.ProgressDataPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GetExerciseProgressUseCase {

    private final SecurityUtils securityUtils;
    private final TrainingSessionRepository trainingSessionRepository;
    private final ExerciseRepository exerciseRepository;

    public GetExerciseProgressUseCase(SecurityUtils securityUtils,
                                      TrainingSessionRepository trainingSessionRepository,
                                      ExerciseRepository exerciseRepository) {
        this.securityUtils = securityUtils;
        this.trainingSessionRepository = trainingSessionRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @Transactional(readOnly = true)
    public ExerciseProgressResponse execute(Long exerciseId, int monthsBack) {
        Long memberId = securityUtils.getCurrentUser().getId();

        Exercise exercise = findExerciseOrThrow(exerciseId);

        // Traemos los datos de los últimos 6 meses.
        LocalDateTime since = LocalDateTime.now().minusMonths(monthsBack);

        // Sesiones donde el alumno hizo ESTE ejercicio en ese rango de tiempo
        List<TrainingSession> sessions = trainingSessionRepository
                .findSessionsByMemberAndExercise(memberId, exerciseId, since);

        List<ProgressDataPoint> dataPoints = new ArrayList<>();

        for (TrainingSession session : sessions) {
            BigDecimal averageE1RM = session.calculateAverageE1RMForExercise(exerciseId);

            if (averageE1RM.compareTo(BigDecimal.ZERO) > 0) {
                dataPoints.add(new ProgressDataPoint(session.getStartTime().toLocalDate(), averageE1RM));
            }
        }

        return new ExerciseProgressResponse(exercise.getId(), exercise.getName(), dataPoints);
    }

    private Exercise findExerciseOrThrow(Long exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseNotFoundException(exerciseId));
    }
}