package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.routine.ActivateRoutineRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ActivateRoutineUseCase {

    private final RoutineRepository routineRepository;

    public ActivateRoutineUseCase(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    @Transactional
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