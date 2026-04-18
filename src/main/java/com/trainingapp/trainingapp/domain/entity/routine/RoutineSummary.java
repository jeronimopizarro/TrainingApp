package com.trainingapp.trainingapp.domain.entity.routine;

import com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus;

public record RoutineSummary(Long id, String name, RoutineStatus status, Long memberId, String memberName) {
}