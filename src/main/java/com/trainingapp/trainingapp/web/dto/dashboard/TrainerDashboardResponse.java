package com.trainingapp.trainingapp.web.dto.dashboard;

import com.trainingapp.trainingapp.domain.enums.routine.ExperienceLevel;

import java.time.LocalDateTime;
import java.util.List;

public record TrainerDashboardResponse(
        List<PendingRoutineRequestDTO> pendingRequests
) {
    public record PendingRoutineRequestDTO(
            Long requestId,
            Long memberId,
            String memberFullName,
            LocalDateTime requestDate,
            Long targetTrainerId,
            Integer availableDays,
            ExperienceLevel experienceLevel,
            String injuries,
            String primaryGoal
    ) {}
}