package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper;

import com.trainingapp.trainingapp.domain.entity.Routine;
import com.trainingapp.trainingapp.domain.entity.RoutineDetail;
import com.trainingapp.trainingapp.domain.entity.TrainingDay;
import com.trainingapp.trainingapp.web.dto.RoutineResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoutineResponseMapper {
    // Este es el único método público que los Casos de Uso van a llamar
    public RoutineResponse toDto(Routine routine) {
        List<RoutineResponse.TrainingDayResponse> daysResponse = routine.getDays().stream()
                .map(this::toTrainingDayResponse)
                .toList();

        return new RoutineResponse(
                routine.getId(), routine.getName(), routine.getStartDate(),
                routine.getEndDate(), routine.getMemberId(), routine.getTrainerId(), routine.getCreatedByUserId(),
                routine.getStatus(), daysResponse
        );
    }

    // Métodos privados internos para mantener el código limpio
    private RoutineResponse.TrainingDayResponse toTrainingDayResponse(TrainingDay day) {
        List<RoutineResponse.RoutineDetailResponse> detailsResponse = day.getDetails().stream()
                .map(this::toRoutineDetailResponse)
                .toList();

        return new RoutineResponse.TrainingDayResponse(
                day.getId(),
                day.getName(),
                day.getOrderNumber(),
                detailsResponse
        );
    }

    private RoutineResponse.RoutineDetailResponse toRoutineDetailResponse(RoutineDetail detail) {
        return new RoutineResponse.RoutineDetailResponse(
                detail.getExerciseId(),
                detail.getSets(),
                detail.getRepsMin(),
                detail.getRepsMax(),
                detail.getTargetRIR(),
                detail.getSuggestedWeight(),
                detail.getNotes()
        );
    }
}
