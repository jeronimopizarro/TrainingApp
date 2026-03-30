package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.application.mapper.routine.RoutineDTOMapper;
import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
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
    private final RoutineAccessValidator accessValidator;
    private final RoutineDTOMapper routineDTOMapper;

    public DuplicateRoutineUseCase(RoutineRepository routineRepository, SecurityUtils securityUtils,
                                   RoutineAccessValidator accessValidator,
                                   RoutineDTOMapper routineDTOMapper) {
        this.routineRepository = routineRepository;
        this.securityUtils = securityUtils;
        this.accessValidator = accessValidator;
        this.routineDTOMapper = routineDTOMapper;
    }

    @Transactional
    public CreateRoutineResponse execute(Long sourceRoutineId, DuplicateRoutineRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        Routine sourceRoutine = findRoutineOrThrow(sourceRoutineId);

        securityUtils.validateSameGym(sourceRoutine.getGymId());
        accessValidator.validateTargetMemberAccess(request.targetMemberId());
        // Determinamos quien es el entrenador.
        Long targetTrainerId = resolveTargetTrainer(request, sourceRoutine);
        accessValidator.validateTargetTrainerAccess(targetTrainerId);

        Routine newRoutine = sourceRoutine.duplicate(request.newName(), request.targetMemberId(),
                targetTrainerId, currentUser.getId());

        Routine savedRoutine = routineRepository.save(newRoutine);
        return routineDTOMapper.toResponse(savedRoutine, "Routine duplicated successfully");
    }

    private Routine findRoutineOrThrow(Long sourceRoutineId) {
        return routineRepository.findById(sourceRoutineId).orElseThrow(
                () -> new RoutineNotFoundException(sourceRoutineId));
    }

    private static Long resolveTargetTrainer(DuplicateRoutineRequest request, Routine sourceRoutine) {
        return request.trainerId() != null ? request.trainerId() : sourceRoutine.getTrainerId();
    }
}