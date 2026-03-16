package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.routine;

import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.routine.RoutineDetail;
import com.trainingapp.trainingapp.domain.entity.routine.TrainingDay;
import com.trainingapp.trainingapp.web.dto.routine.RoutineResponse;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class RoutineResponseMapper {

    public RoutineResponse toResponse(Routine routine) {
        List<RoutineResponse.TrainingDayResponse> daysResponse = routine.getDays().stream()
                .map(this::toTrainingDayResponse)
                .toList();

        return new RoutineResponse(
                routine.getId(), routine.getName(), routine.getStartDate(),
                routine.getEndDate(), routine.getMemberId(), routine.getTrainerId(), routine.getCreatedByUserId(),
                routine.getStatus(), daysResponse
        );
    }

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
                detail.getId(),
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