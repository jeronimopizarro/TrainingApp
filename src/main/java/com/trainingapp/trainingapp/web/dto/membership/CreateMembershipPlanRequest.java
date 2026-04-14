package com.trainingapp.trainingapp.web.dto.membership;

import java.math.BigDecimal;

public record CreateMembershipPlanRequest(
    String name,
    String description,
    BigDecimal price,
    Integer durationMonths,
    Long gymId
) {}
