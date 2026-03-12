package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.routine.ActivateRoutineRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ActivateRoutineUseCase {

    private final RoutineRepository routineRepository;
    private final RoutineAccessValidator accessValidator;

    public ActivateRoutineUseCase(RoutineRepository routineRepository,
                                  RoutineAccessValidator accessValidator) {
        this.routineRepository = routineRepository;
        this.accessValidator = accessValidator;
    }

    @Transactional
    public void execute(Long id, ActivateRoutineRequest request) {
        Routine routine = findRoutineOrThrow(id);

        accessValidator.validateModificationPermission(routine);

        routine.activate(request.startDate(), request.endDate());

        routineRepository.save(routine);
    }


    private Routine findRoutineOrThrow(Long id) {
        return routineRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Routine with id " + id + " not found."));
    }
}