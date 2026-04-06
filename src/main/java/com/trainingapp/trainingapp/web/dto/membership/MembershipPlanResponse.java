package com.trainingapp.trainingapp.web.dto.membership;

import java.math.BigDecimal;

public record MembershipPlanResponse(Long id, String name, String description, BigDecimal price,
                                     Integer durationMonths, Long gymId, boolean active) {
}