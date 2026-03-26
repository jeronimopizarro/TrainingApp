package com.trainingapp.trainingapp.web.dto.tracker;

import com.trainingapp.trainingapp.domain.enums.tracker.SessionStatus;

import java.time.LocalDateTime;

public record SessionResponse(
        Long id,
        Long memberId,
        Long routineId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        SessionStatus status
) {
}