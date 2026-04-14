package com.trainingapp.trainingapp.web.dto.user.trainer;

import com.trainingapp.trainingapp.domain.enums.user.Role;

public record TrainerResponse(Long id, String firstName, String lastName, String email, String dni, Long gymId,
                              String specialization, Role role, boolean active) {
}
