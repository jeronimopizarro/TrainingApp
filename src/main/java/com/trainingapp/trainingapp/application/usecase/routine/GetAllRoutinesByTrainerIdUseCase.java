package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.routine.RoutineSummary;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.routine.GetAllRoutinesByTrainerIdResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllRoutinesByTrainerIdUseCase {

    private final RoutineRepository routineRepository;
    private final RoutineAccessValidator accessValidator;

    public GetAllRoutinesByTrainerIdUseCase(RoutineRepository routineRepository,
                                            RoutineAccessValidator accessValidator) {
        this.routineRepository = routineRepository;
        this.accessValidator = accessValidator;
    }

    public List<GetAllRoutinesByTrainerIdResponse> execute(Long trainerId) {
        accessValidator.validateTargetTrainerAccess(trainerId);

        List<RoutineSummary> summaries =
                routineRepository.findAllSummariesByTrainerId(trainerId);

        return mapToResponse(summaries);
    }

    private List<GetAllRoutinesByTrainerIdResponse> mapToResponse(List<RoutineSummary> summaries) {
        return summaries.stream()
                .map(s -> new GetAllRoutinesByTrainerIdResponse(
                        s.id(),
                        s.name(),
                        s.status(),
                        s.memberId()))
                .toList();
    }
}