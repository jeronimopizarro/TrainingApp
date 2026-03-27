package com.trainingapp.trainingapp.web.dto.dashboard;

import java.time.LocalDate;
import java.util.List;

public record MemberDashboardResponse(
        Integer daysUntilExpiration,
        ActiveRoutineDTO activeRoutine,
        List<LocalDate> trainingDaysThisMonth
) {
    public record ActiveRoutineDTO(
            Long routineId,
            String name,
            LocalDate endDate
    ) {}
}