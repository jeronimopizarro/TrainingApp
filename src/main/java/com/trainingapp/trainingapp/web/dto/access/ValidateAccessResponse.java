package com.trainingapp.trainingapp.web.dto.access;

public record ValidateAccessResponse(
        boolean accessGranted,
        String memberName,
        String message
) {}