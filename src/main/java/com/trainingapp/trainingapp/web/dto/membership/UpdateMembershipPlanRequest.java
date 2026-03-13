package com.trainingapp.trainingapp.web.dto.membership;

import java.math.BigDecimal;

public record UpdateMembershipPlanRequest(String name, String description, BigDecimal price,
                                          Integer durationDays) {
}
