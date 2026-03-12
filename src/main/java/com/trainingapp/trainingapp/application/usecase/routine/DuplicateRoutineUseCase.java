package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.exception.routine.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.routine.CreateRoutineResponse;
import com.trainingapp.trainingapp.web.dto.routine.DuplicateRoutineRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DuplicateRoutineUseCase {
    private final RoutineRepository routineRepository;
    private final SecurityUtils securityUtils;

    public DuplicateRoutineUseCase(RoutineRepository routineRepository, SecurityUtils securityUtils) {
        this.routineRepository = routineRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public CreateRoutineResponse execute(Long sourceRoutineId, DuplicateRoutineRequest request) {
        Routine sourceRoutine = findRoutineOrThrow(sourceRoutineId);
        // Determinamos quien es el entrenador.
        Long targetTrainerId = resolveTargetTrainer(request, sourceRoutine);

        User currentUser = securityUtils.getCurrentUser();

        Routine newRoutine = sourceRoutine.duplicate(request.newName(), request.targetMemberId(),
                targetTrainerId, currentUser.getId());

        Routine savedRoutine = routineRepository.save(newRoutine);

        return new CreateRoutineResponse(savedRoutine.getId(), "Routine duplicated successfully");
    }

    private static Long resolveTargetTrainer(DuplicateRoutineRequest request, Routine sourceRoutine) {
        return request.trainerId() != null ? request.trainerId() : sourceRoutine.getTrainerId();
    }

    private Routine findRoutineOrThrow(Long sourceRoutineId) {
        return routineRepository.findById(sourceRoutineId).orElseThrow(
                () -> new RoutineNotFoundException(
                        "The routine with id " + sourceRoutineId + " was not found"));
    }
}