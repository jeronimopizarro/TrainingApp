package com.trainingapp.trainingapp.web.dto.dashboard;

import java.time.LocalDate;
import java.util.List;

public record TrainerDashboardResponse(
        List<PendingRoutineRequestDTO> pendingRequests
) {
    public record PendingRoutineRequestDTO(
            Long requestId,
            Long memberId,
            String memberFullName,
            String note,
            LocalDate requestDate
    ) {}
}