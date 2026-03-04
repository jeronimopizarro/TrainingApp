package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.domain.entity.routine.RoutineSummary;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.routine.GetAllRoutinesByTrainerIdResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllRoutinesByTrainerIdUseCase {

    private final RoutineRepository routineRepository;

    public GetAllRoutinesByTrainerIdUseCase(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    public List<GetAllRoutinesByTrainerIdResponse> execute(Long trainerId) {
        List<RoutineSummary> summaries = routineRepository.findAllSummariesByTrainerId(trainerId);

        return summaries.stream()
                .map(s -> new GetAllRoutinesByTrainerIdResponse(s.id(), s.name(), s.status(), s.memberId()))
                .toList();
    }
}