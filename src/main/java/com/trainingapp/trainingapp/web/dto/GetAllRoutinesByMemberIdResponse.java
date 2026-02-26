package com.trainingapp.trainingapp.web.dto;

import com.trainingapp.trainingapp.domain.Enum.RoutineStatus;

public record GetAllRoutinesByMemberIdResponse(Long id, String name, RoutineStatus status) {}