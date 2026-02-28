package com.trainingapp.trainingapp.application.usecase;

import com.trainingapp.trainingapp.domain.Enum.RoutineStatus;
import com.trainingapp.trainingapp.domain.entity.Routine;
import com.trainingapp.trainingapp.domain.repository.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.ActivateRoutineRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ActivateRoutineUseCase {

    private final RoutineRepository routineRepository;

    public ActivateRoutineUseCase(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    public void execute(Long routineId, ActivateRoutineRequest request) {
        Routine routine = routineRepository.findById(routineId).orElseThrow(
                () -> new EntityNotFoundException("Routine with id " + routineId + " not found."));

        boolean hasActiveRoutine = routineRepository.existsByMemberIdAndStatus(
                routine.getMemberId(), RoutineStatus.ACTIVE);

        if (hasActiveRoutine) {
            throw new IllegalStateException("The member already has an active routine. Please archive or complete the current one before activating a new one.");
        }

        routine.activate(request.requestingUserId(), request.startDate(), request.endDate());

        routineRepository.save(routine);
    }
}
