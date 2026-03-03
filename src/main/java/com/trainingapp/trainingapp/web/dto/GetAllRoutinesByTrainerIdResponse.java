package com.trainingapp.trainingapp.web.dto;

import com.trainingapp.trainingapp.domain.Enum.RoutineStatus;

public record GetAllRoutinesByTrainerIdResponse(Long id,
                                                String name,
                                                RoutineStatus status,
                                                Long memberId) {
}