package com.trainingapp.trainingapp.web.dto.access;

import java.time.LocalDateTime;

public record AccessLogResponse(
        Long id,
        Long memberId,
        String memberFirstName,
        String memberLastName,
        LocalDateTime timestamp,
        boolean accessGranted,
        String message
) {
}
