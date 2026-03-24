package com.trainingapp.trainingapp.web.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductRequest(
        @NotBlank(message = "El nombre del producto es obligatorio")
        String name,

        String description,

        @NotNull(message = "El precio es obligatorio")
        @Min(value = 0, message = "El precio no puede ser negativo")
        Double price,

        @NotNull(message = "El stock inicial es obligatorio")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock,

        String imageUrl,

        @NotNull(message = "El ID del gimnasio es obligatorio")
        Long gymId
) {
}