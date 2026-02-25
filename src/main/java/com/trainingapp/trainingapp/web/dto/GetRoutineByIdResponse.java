package com.trainingapp.trainingapp.web.dto;

import com.trainingapp.trainingapp.domain.Enum.RoutineStatus;

import java.time.LocalDate;
import java.util.List;

public record GetRoutineByIdResponse(Long id, String name, LocalDate startDate, LocalDate endDate,
                                     Long memberId, Long trainerId, RoutineStatus status,
                                     List<TrainingDayResponse> days) {

    public record TrainingDayResponse(
            Long id, String name, Integer orderNumber, List<RoutineDetailResponse> exercises
    ) {}

    public record RoutineDetailResponse(
            Long exerciseId, Integer sets, Integer repsMin, Integer repsMax, String notes
    ) {}
}