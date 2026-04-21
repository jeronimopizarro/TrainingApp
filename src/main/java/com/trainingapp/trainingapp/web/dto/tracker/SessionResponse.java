package com.trainingapp.trainingapp.web.dto.tracker;

import com.trainingapp.trainingapp.domain.enums.tracker.SessionStatus;

import java.time.Instant;
import java.util.List;

public record SessionResponse(
        Long id,
        Long memberId,
        Long routineId,
        Long trainingDayId,
        Instant startTime,
        Instant endTime,
        SessionStatus status,
        List<SetLogResponse> loggedSets
) {
}
