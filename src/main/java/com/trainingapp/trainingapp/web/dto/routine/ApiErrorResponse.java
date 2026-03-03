package com.trainingapp.trainingapp.web.dto.routine;

import java.time.LocalDateTime;

public record ApiErrorResponse(int statusCode, String error, String message,
                               LocalDateTime timestamp) {
}