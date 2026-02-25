package com.trainingapp.trainingapp.application.usecase;

import com.trainingapp.trainingapp.domain.entity.Routine;
import com.trainingapp.trainingapp.domain.entity.RoutineDetail;
import com.trainingapp.trainingapp.domain.entity.TrainingDay;
import com.trainingapp.trainingapp.domain.exception.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.GetRoutineByIdResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRoutineByIdUseCase {
    private final RoutineRepository routineRepository;

    public GetRoutineByIdUseCase(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    public GetRoutineByIdResponse execute(Long id) {
        Routine routine = routineRepository.findById(id)
                .orElseThrow(() -> new RoutineNotFoundException("The routine with id " + id + " was not found"));

        // Mapeamos la lista de días delegando la lógica a un método privado
        List<GetRoutineByIdResponse.TrainingDayResponse> daysResponse = routine.getDays().stream()
                .map(this::mapToTrainingDayResponse)
                .toList();

        return new GetRoutineByIdResponse(
                routine.getId(), routine.getName(), routine.getStartDate(),
                routine.getEndDate(), routine.getMemberId(), routine.getTrainerId(),
                routine.getStatus(), daysResponse
        );
    }

    private GetRoutineByIdResponse.TrainingDayResponse mapToTrainingDayResponse(TrainingDay day) {
        // Mapeamos los ejercicios de este día en particular
        List<GetRoutineByIdResponse.RoutineDetailResponse> detailsResponse = day.getDetails().stream()
                .map(this::mapToRoutineDetailResponse)
                .toList();

        return new GetRoutineByIdResponse.TrainingDayResponse(
                day.getId(),
                day.getName(),
                day.getOrderNumber(),
                detailsResponse
        );
    }

    private GetRoutineByIdResponse.RoutineDetailResponse mapToRoutineDetailResponse(
            RoutineDetail detail) {
        return new GetRoutineByIdResponse.RoutineDetailResponse(
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