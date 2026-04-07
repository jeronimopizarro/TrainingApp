package com.trainingapp.trainingapp.web.dto.tracker;

import java.math.BigDecimal;
import java.util.List;

public record MemberProgressSummaryResponse(
        List<ExerciseSummaryDTO> exercises
) {
    public record ExerciseSummaryDTO(
            Long exerciseId,
            String exerciseName,
            BigDecimal currentPersonalRecord // Mejor promedio reciente
    ) {}
}