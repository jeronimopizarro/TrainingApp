package com.trainingapp.trainingapp.application.usecase;

import com.trainingapp.trainingapp.domain.entity.Routine;
import com.trainingapp.trainingapp.domain.exception.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.RoutineRepository;
import org.springframework.stereotype.Service;

@Service
public class InactiveRoutineUseCase {

    private final RoutineRepository routineRepository;

    public InactiveRoutineUseCase(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    public void execute(Long routineId, Long requestingUserId) {
        Routine routine = routineRepository.findById(routineId).orElseThrow(
                () -> new RoutineNotFoundException(
                        "The routine with id " + routineId + " was not found"));

        routine.inactive(requestingUserId);
        routineRepository.save(routine);
    }
}
