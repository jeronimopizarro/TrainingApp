package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.application.mapper.routine.RoutineDTOMapper;
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
    private final RoutineDTOMapper routineDTOMapper;

    public GetAllRoutinesByTrainerIdUseCase(RoutineRepository routineRepository,
                                            RoutineAccessValidator accessValidator,
                                            RoutineDTOMapper routineDTOMapper) {
        this.routineRepository = routineRepository;
        this.accessValidator = accessValidator;
        this.routineDTOMapper = routineDTOMapper;
    }

    public List<GetAllRoutinesByTrainerIdResponse> execute(Long trainerId) {
        accessValidator.validateTargetTrainerAccess(trainerId);

        List<RoutineSummary> summaries = routineRepository.findAllSummariesByTrainerId(trainerId);

        return summaries.stream()
                .map(routineDTOMapper::toAllRoutinesByTrainerIdResponse)
                .toList();
    }
}