package com.trainingapp.trainingapp.web.dto.access;

import com.trainingapp.trainingapp.domain.enums.access.AccessMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ValidateAccessRequest(
        @NotBlank(message = "El identificador es obligatorio") String identifier,// Acá viene el DNI o el Token JWT
        @NotNull(message = "El método de acceso es obligatorio") AccessMethod method
) {}