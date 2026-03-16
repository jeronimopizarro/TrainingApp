package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.application.mapper.routine.RoutineDTOMapper;
import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.exception.routine.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;;
import com.trainingapp.trainingapp.web.dto.routine.RoutineResponse;
import org.springframework.stereotype.Service;

@Service
public class GetActiveRoutineUseCase {

    private final RoutineRepository routineRepository;
    private final RoutineDTOMapper routineDTOMapper;
    private final RoutineAccessValidator accessValidator;

    public GetActiveRoutineUseCase(RoutineRepository routineRepository,
                                   RoutineDTOMapper routineDTOMapper,
                                   RoutineAccessValidator accessValidator) {

        this.routineRepository = routineRepository;
        this.routineDTOMapper = routineDTOMapper;
        this.accessValidator = accessValidator;
    }

    public RoutineResponse execute(Long memberId) {
        accessValidator.validateTargetMemberAccess(memberId);

        Routine routine = findActiveRoutineOrThrow(memberId);

        return routineDTOMapper.toResponse(routine);
    }

    private Routine findActiveRoutineOrThrow(Long memberId) {
        return routineRepository.findByMemberIdAndStatus(memberId,
                RoutineStatus.ACTIVE).orElseThrow(() -> new RoutineNotFoundException(
                "No active routine found for member with ID: " + memberId));
    }
}