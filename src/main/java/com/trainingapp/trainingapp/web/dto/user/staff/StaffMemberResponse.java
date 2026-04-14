package com.trainingapp.trainingapp.web.dto.user.staff;

import com.trainingapp.trainingapp.domain.enums.user.Role;

public record StaffMemberResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    String dni,
    Role role,
    String specialization, // Solo para TRAINER
    boolean active
) {}
