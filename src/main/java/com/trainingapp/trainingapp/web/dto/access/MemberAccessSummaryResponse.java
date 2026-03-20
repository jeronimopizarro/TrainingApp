package com.trainingapp.trainingapp.web.dto.access;

import java.util.List;

public record MemberAccessSummaryResponse(
        long totalVisits,
        List<AccessLogResponse> logs
) {
}