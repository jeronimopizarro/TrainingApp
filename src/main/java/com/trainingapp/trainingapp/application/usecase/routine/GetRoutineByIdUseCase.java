package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.exception.routine.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.routine.RoutineResponseMapper;
import com.trainingapp.trainingapp.web.dto.routine.RoutineResponse;
import org.springframework.stereotype.Service;

@Service
public class GetRoutineByIdUseCase {

    private final RoutineRepository routineRepository;
    private final RoutineResponseMapper mapper;

    public GetRoutineByIdUseCase(RoutineRepository routineRepository,
                                 RoutineResponseMapper mapper) {
        this.routineRepository = routineRepository;
        this.mapper = mapper;
    }

    public RoutineResponse execute(Long id) {
        Routine routine = routineRepository.findById(id)
                .orElseThrow(() -> new RoutineNotFoundException(
                        "The routine with id " + id + " was not found"));

        return mapper.toDto(routine);
    }
}