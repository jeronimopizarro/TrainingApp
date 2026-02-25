package com.trainingapp.trainingapp.web.dto;

public record CreateRoutineRequest(
        //Routine
        String name, Long memberId, Long trainerId,
        //TrainingDay
        String dayName,
        //Routine detail
        Long exerciseId, int sets, int repsMin, int repsMax, int targetRIR, Double suggestedWeight,
        String notes) {}