package com.trainingapp.trainingapp.web.dto.routine;

import com.trainingapp.trainingapp.domain.Enum.routine.RoutineStatus;

public record GetAllRoutinesByMemberIdResponse(Long id, String name, RoutineStatus status) {
}