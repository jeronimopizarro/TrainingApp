package com.trainingapp.trainingapp.web.dto.user.receptionist;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterReceptionistRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String dni,
        @NotNull Long gymId
) {}