package com.trainingapp.trainingapp.web.dto.routine;

import com.trainingapp.trainingapp.domain.Enum.routine.RoutineStatus;
import java.time.LocalDate;
import java.util.List;

public record RoutineResponse(Long id, String name, LocalDate startDate, LocalDate endDate,
                              Long memberId, Long trainerId, Long createdByUserId,
                              RoutineStatus status,
                              List<TrainingDayResponse> days) {

    public record TrainingDayResponse(
            Long id, String name, Integer orderNumber, List<RoutineDetailResponse> exercises
    ) {
    }

    public record RoutineDetailResponse(
            Long id, Long exerciseId, Integer sets, Integer repsMin, Integer repsMax, Integer targetRIR,
            Double suggestedWeight, String notes
    ) {
    }
}