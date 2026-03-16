package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.exception.routine.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.routine.RoutineResponseMapper;
import com.trainingapp.trainingapp.web.dto.routine.RoutineResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class InactiveRoutineUseCase {

    private final RoutineRepository routineRepository;
    private final RoutineAccessValidator accessValidator;
    private final RoutineResponseMapper routineMapper;

    public InactiveRoutineUseCase(RoutineRepository routineRepository,
                                  RoutineAccessValidator accessValidator,
                                  RoutineResponseMapper routineMapper){
        this.routineRepository = routineRepository;
        this.accessValidator = accessValidator;
        this.routineMapper = routineMapper;
    }

    @Transactional
    public RoutineResponse execute(Long id) {
        Routine routine = findRoutineOrThrow(id);
        accessValidator.validateModificationPermission(routine);

        routine.inactive();

        Routine savedRoutine = routineRepository.save(routine);
        return routineMapper.toResponse(savedRoutine);
    }

    private Routine findRoutineOrThrow(Long id) {
        return routineRepository.findById(id).orElseThrow(
                () -> new RoutineNotFoundException(
                        "The routine with id " + id + " was not found"));
    }
}
