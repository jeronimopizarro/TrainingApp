package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.application.mapper.routine.RoutineDTOMapper;
import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.exception.routine.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.routine.RoutineResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class InactiveRoutineUseCase {

    private final RoutineRepository routineRepository;
    private final RoutineAccessValidator accessValidator;
    private final RoutineDTOMapper routineDTOMapper;

    public InactiveRoutineUseCase(RoutineRepository routineRepository,
                                  RoutineAccessValidator accessValidator,
                                  RoutineDTOMapper routineDTOMapper){
        this.routineRepository = routineRepository;
        this.accessValidator = accessValidator;
        this.routineDTOMapper = routineDTOMapper;
    }

    @Transactional
    public RoutineResponse execute(Long id) {
        Routine routine = findRoutineOrThrow(id);
        accessValidator.validateModificationPermission(routine);

        routine.inactive();

        Routine savedRoutine = routineRepository.save(routine);
        return routineDTOMapper.toResponse(savedRoutine);
    }

    private Routine findRoutineOrThrow(Long id) {
        return routineRepository.findById(id).orElseThrow(
                () -> new RoutineNotFoundException(
                        "The routine with id " + id + " was not found"));
    }
}