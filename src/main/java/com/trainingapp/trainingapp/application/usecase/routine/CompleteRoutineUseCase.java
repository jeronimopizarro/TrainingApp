package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.exception.routine.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CompleteRoutineUseCase {

    private final RoutineRepository routineRepository;
    private final RoutineAccessValidator accessValidator;

    public CompleteRoutineUseCase(RoutineRepository routineRepository,
                                  RoutineAccessValidator accessValidator) {
        this.routineRepository = routineRepository;
        this.accessValidator = accessValidator;
    }

    @Transactional
    public void execute(Long id){
        Routine routine = findRoutineOrThrow(id);

        accessValidator.validateModificationPermission(routine);

        routine.complete();

        routineRepository.save(routine);
    }

    private Routine findRoutineOrThrow(Long id) {
        return routineRepository.findById(id)
                .orElseThrow(() -> new RoutineNotFoundException(
                        "The routine with id " + id + " was not found"));
    }
}