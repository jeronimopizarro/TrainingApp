package com.trainingapp.trainingapp.web.dto.tracker;

import java.math.BigDecimal;

public record SetLogResponse(
        Long id,
        Long exerciseId,
        Integer setNumber,
        Integer repsPerformed,
        BigDecimal weightLifted,
        String notes
) {
}