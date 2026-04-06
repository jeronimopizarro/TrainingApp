package com.trainingapp.trainingapp.web.dto.tracker;

public record StartSessionRequest(
        Long routineId,
        Long trainingDayId
) {
}