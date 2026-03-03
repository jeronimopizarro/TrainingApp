package com.trainingapp.trainingapp.web.dto.routine;

import com.trainingapp.trainingapp.domain.Enum.routine.RoutineStatus;

public record GetAllRoutinesByTrainerIdResponse(Long id,
                                                String name,
                                                RoutineStatus status,
                                                Long memberId) {
}