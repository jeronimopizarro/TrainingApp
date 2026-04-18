package com.trainingapp.trainingapp.web.dto.routine;

import com.trainingapp.trainingapp.domain.enums.routine.ExperienceLevel;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineRequestStatus;

import java.time.LocalDateTime;

public record GetPendingRoutineRequestsResponse(
        Long id,
        Long memberId,
        String memberName,
        LocalDateTime requestDate,
        RoutineRequestStatus status,
        Long targetTrainerId,
        Integer availableDays,
        ExperienceLevel experienceLevel,
        String injuries,
        String primaryGoal
) {}
