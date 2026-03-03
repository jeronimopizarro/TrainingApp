package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.domain.Enum.routine.RoutineStatus;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.exception.routine.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.routine.RoutineResponseMapper;
import com.trainingapp.trainingapp.web.dto.routine.RoutineResponse;
import org.springframework.stereotype.Service;

@Service
public class GetActiveRoutineUseCase {

    private final RoutineRepository routineRepository;
    private final RoutineResponseMapper mapper;

    public GetActiveRoutineUseCase(RoutineRepository routineRepository,
                                   RoutineResponseMapper mapper) {

        this.routineRepository = routineRepository;
        this.mapper = mapper;
    }

    public RoutineResponse execute(Long memberId) {
        Routine routine = routineRepository.findByMemberIdAndStatus(memberId,
                RoutineStatus.ACTIVE).orElseThrow(() -> new RoutineNotFoundException(
                "No active routine found for member with ID: " + memberId));

        return mapper.toDto(routine);
    }
}