package com.trainingapp.trainingapp.web.dto.product;

public record ProductResponse(
        Long id,
        String name,
        String description,
        Double price,
        Integer stock,
        String imageUrl,
        boolean isActive,
        Long gymId
) {
}