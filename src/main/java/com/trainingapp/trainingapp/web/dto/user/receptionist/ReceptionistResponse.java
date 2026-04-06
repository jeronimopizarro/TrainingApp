package com.trainingapp.trainingapp.web.dto.user.receptionist;

public record ReceptionistResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String dni,
        Long gymId
) {}