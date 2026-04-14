package com.trainingapp.trainingapp.web.dto.user.receptionist;

import com.trainingapp.trainingapp.domain.enums.user.Role;

public record ReceptionistResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String dni,
        Long gymId,
        Role role,
        boolean active
) {}
