package com.trainingapp.trainingapp.web.dto.access;

import java.time.LocalDateTime;

public record AccessLogResponse(
        Long id,
        Long memberId,
        LocalDateTime timestamp,
        boolean accessGranted,
        String message
) {
}