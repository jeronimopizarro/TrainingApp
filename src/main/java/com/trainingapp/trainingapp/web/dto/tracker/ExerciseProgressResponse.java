package com.trainingapp.trainingapp.web.dto.tracker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ExerciseProgressResponse(
        Long exerciseId,
        String exerciseName,
        List<ProgressDataPoint> dataPoints
) {
    public record ProgressDataPoint(
            LocalDate date,
            BigDecimal e1rm
    ) {}
}