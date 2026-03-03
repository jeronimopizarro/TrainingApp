package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.exception.routine.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteRoutineUseCase {

    private final RoutineRepository routineRepository;

    public DeleteRoutineUseCase(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    public void execute(Long routineId, Long requestingUserId) {
        Routine routine = routineRepository.findById(routineId).orElseThrow(
                () -> new RoutineNotFoundException(
                        "The routine with id " + routineId + " was not found"));

        routine.validateForDeletion(requestingUserId);

        routineRepository.delete(routine);
    }
}