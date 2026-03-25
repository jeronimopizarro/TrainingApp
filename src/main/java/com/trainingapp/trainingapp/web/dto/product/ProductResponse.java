package com.trainingapp.trainingapp.web.dto.product;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        String imageUrl,
        boolean isActive,
        Long gymId
) {
}