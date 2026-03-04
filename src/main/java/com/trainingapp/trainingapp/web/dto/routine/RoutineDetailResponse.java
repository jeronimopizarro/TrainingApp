package com.trainingapp.trainingapp.web.dto.routine;

import com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus;
import java.time.LocalDate;
import java.util.List;

public record RoutineDetailResponse(Long id, String name, LocalDate startDate, LocalDate endDate,
                                    Long memberId, Long trainerId,
                                    Long createdByUserId, RoutineStatus status,
                                    List<DayDetailResponse> days
) {
    public record DayDetailResponse(Long id, String name, Integer orderNumber,
                                    List<ExerciseItemResponse> exercises
    ) {
    }

    public record ExerciseItemResponse(Integer orderNumber, Integer sets, Integer repsMin, Integer repsMax,
                                       Integer targetRIR,
                                       Double suggestedWeight, String notes,
                                       Long exerciseId, String exerciseName,
                                       String exerciseImageUrl,
                                       String exerciseVideoUrl
    ) {
    }
}