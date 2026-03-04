package com.trainingapp.trainingapp.web.dto.routine;

import com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus;

public record GetAllRoutinesByMemberIdResponse(Long id, String name, RoutineStatus status) {
}