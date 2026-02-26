package com.trainingapp.trainingapp.application.usecase;

import com.trainingapp.trainingapp.domain.entity.Routine;
import com.trainingapp.trainingapp.domain.repository.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.ActivateRoutineRequest;
import org.springframework.stereotype.Service;

@Service
public class ActivateRoutineUseCase {
    private final RoutineRepository routineRepository;

    public ActivateRoutineUseCase(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    public void execute(Long routineId, ActivateRoutineRequest request) {
        Routine routine = routineRepository.findById(routineId).orElseThrow(
                () -> new RuntimeException("Routine with id " + routineId + " not found."));

        routine.activate(request.requestingUserId(), request.startDate(), request.endDate());

        routineRepository.save(routine);
    }
}
