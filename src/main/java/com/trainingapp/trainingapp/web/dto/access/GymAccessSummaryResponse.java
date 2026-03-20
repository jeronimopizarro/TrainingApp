package com.trainingapp.trainingapp.web.dto.access;

import java.util.List;

public record GymAccessSummaryResponse(
        long totalSuccessfulEntriesToday, // ¡Tu idea!
        long totalFailedAttemptsToday,    // De paso sumamos los rechazos
        List<AccessLogResponse> logs      // El historial detallado
) {
}
