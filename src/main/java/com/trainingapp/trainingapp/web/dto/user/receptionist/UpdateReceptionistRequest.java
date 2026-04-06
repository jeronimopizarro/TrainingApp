package com.trainingapp.trainingapp.web.dto.user.receptionist;

import jakarta.validation.constraints.NotBlank;

public record UpdateReceptionistRequest(
        @NotBlank(message = "El nombre no puede estar vacío") String firstName,
        @NotBlank(message = "El apellido no puede estar vacío") String lastName,
        @NotBlank(message = "El DNI no puede estar vacío") String dni
) {}