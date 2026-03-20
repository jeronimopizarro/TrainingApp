package com.trainingapp.trainingapp.web.dto.user.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RegisterMemberRequest(
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName,
        @NotBlank(message = "Email is required") @Email String email,
        @NotBlank(message = "Password is required") String password,
        @NotBlank(message = "DNI is required") String dni,
        @NotNull(message = "Gym ID is required") Long gymId,
        LocalDate birthDate,
        String primaryGoal
) {
}