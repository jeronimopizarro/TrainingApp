package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.application.mapper.routine.RoutineDTOMapper;
import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.routine.RoutineSummary;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.routine.GetAllRoutinesByMemberIdResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllRoutinesByMemberIdUseCase {

    private final RoutineRepository routineRepository;
    private final RoutineAccessValidator accessValidator;
    private final RoutineDTOMapper routineDTOMapper;

    public GetAllRoutinesByMemberIdUseCase(RoutineRepository routineRepository,
                                           RoutineAccessValidator accessValidator,
                                           RoutineDTOMapper routineDTOMapper) {
        this.routineRepository = routineRepository;
        this.accessValidator = accessValidator;
        this.routineDTOMapper = routineDTOMapper;
    }

    public List<GetAllRoutinesByMemberIdResponse> execute(Long memberId) {
        accessValidator.validateTargetMemberAccess(memberId);

        List<RoutineSummary> summaries = routineRepository.findAllSummariesByMemberId(memberId);

        return summaries.stream()
                .map(routineDTOMapper::toAllRoutinesByMemberIdResponse)
                .toList();
    }
}