package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.application.mapper.routine.RoutineDTOMapper;
import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.routine.TrainingDay;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseNotFoundException;
import com.trainingapp.trainingapp.domain.exception.exercise.UnauthorizedExerciseAccessException;
import com.trainingapp.trainingapp.domain.exception.routine.InvalidRoutineStateException;
import com.trainingapp.trainingapp.domain.exception.routine.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.routine.CreateRoutineResponse;
import com.trainingapp.trainingapp.web.dto.routine.UpdateRoutineRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdateRoutineUseCase {

    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;
    private final SecurityUtils securityUtils;
    private final RoutineAccessValidator accessValidator;
    private final RoutineDTOMapper routineDTOMapper;

    public UpdateRoutineUseCase(RoutineRepository routineRepository,
                                ExerciseRepository exerciseRepository, SecurityUtils securityUtils,
                                RoutineAccessValidator accessValidator,
                                RoutineDTOMapper routineDTOMapper) {
        this.routineRepository = routineRepository;
        this.exerciseRepository = exerciseRepository;
        this.securityUtils = securityUtils;
        this.accessValidator = accessValidator;
        this.routineDTOMapper = routineDTOMapper;
    }

    @Transactional
    public CreateRoutineResponse execute(Long routineId,
                                         UpdateRoutineRequest request) {
        Routine routine = validateRoutine(routineId);
        accessValidator.validateModificationPermission(routine);

        validateRoutineStatusForUpdate(routine);

        Long currentUserGymId = securityUtils.getCurrentUserGymId();
        User currentUser = securityUtils.getCurrentUser();
        validateExercises(request, currentUserGymId, currentUser);

        List<TrainingDay> mappedDays = routineDTOMapper.toDomainDays(request.days());

        routine.update(request.name(), request.trainerId(), mappedDays);
        Routine updatedRoutine = routineRepository.save(routine);
        return routineDTOMapper.toResponse(updatedRoutine, "Routine updated successfully");
    }

    private Routine validateRoutine(Long id) {
        return routineRepository.findById(id).orElseThrow(
                () -> new RoutineNotFoundException(id));
    }

    private void validateRoutineStatusForUpdate(Routine routine) {
        if (routine.getStatus() != RoutineStatus.DRAFT && routine.getStatus() != RoutineStatus.ACTIVE) {
            throw new InvalidRoutineStateException();
        }
    }

    private void validateExercises(UpdateRoutineRequest request, Long gymId, User currentUser) {
        request.days().forEach(day -> {
            day.exercises().forEach(exReq -> {
                Exercise exercise = exerciseRepository.findById(exReq.exerciseId())
                        .orElseThrow(() -> new ExerciseNotFoundException(exReq.exerciseId()));

                // Solo se permiten ejercicios de nuestro GYM.
                if (!currentUser.isSuperAdmin()) {
                    if (!exercise.getIsBase() && !gymId.equals(exercise.getGymId())) {
                        throw new UnauthorizedExerciseAccessException();
                    }
                }
            });
        });
    }
}