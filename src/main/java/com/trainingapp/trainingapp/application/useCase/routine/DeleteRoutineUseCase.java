package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.exception.routine.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DeleteRoutineUseCase {

    private final RoutineRepository routineRepository;
    private final RoutineAccessValidator accessValidator;

    public DeleteRoutineUseCase(RoutineRepository routineRepository,
                                RoutineAccessValidator accessValidator) {
        this.routineRepository = routineRepository;
        this.accessValidator = accessValidator;
    }

    @Transactional
    public void execute(Long id) {
        Routine routine = findRoutineOrThrow(id);
        accessValidator.validateModificationPermission(routine);

        routine.validateForDeletion();
        routine.deactivate();

        routineRepository.save(routine);
    }

    private Routine findRoutineOrThrow(Long id) {
        return routineRepository.findById(id).orElseThrow(
                () -> new RoutineNotFoundException(
                        "The routine with id " + id + " was not found"));
    }
}