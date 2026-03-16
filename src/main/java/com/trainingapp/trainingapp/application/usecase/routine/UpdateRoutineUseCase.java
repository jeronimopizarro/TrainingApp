package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.routine.RoutineDetail;
import com.trainingapp.trainingapp.domain.entity.routine.TrainingDay;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseNotFoundException;
import com.trainingapp.trainingapp.domain.exception.routine.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.routine.RoutineMapper;
import com.trainingapp.trainingapp.web.dto.routine.CreateRoutineResponse;
import com.trainingapp.trainingapp.web.dto.routine.UpdateRoutineRequest;
import com.trainingapp.trainingapp.web.dto.routine.UpdateRoutineRequest.UpdateTrainingDayRequest;
import com.trainingapp.trainingapp.web.dto.routine.UpdateRoutineRequest.UpdateRoutineDetailRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UpdateRoutineUseCase {

    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;
    private final SecurityUtils securityUtils;
    private final RoutineAccessValidator accessValidator;
    private final RoutineMapper routineMapper;

    public UpdateRoutineUseCase(RoutineRepository routineRepository,
                                ExerciseRepository exerciseRepository, SecurityUtils securityUtils,
                                RoutineAccessValidator accessValidator, RoutineMapper routineMapper) {
        this.routineRepository = routineRepository;
        this.exerciseRepository = exerciseRepository;
        this.securityUtils = securityUtils;
        this.accessValidator = accessValidator;
        this.routineMapper = routineMapper;
    }

    @Transactional
    public CreateRoutineResponse execute(Long routineId,
                                         UpdateRoutineRequest request) {
        Routine routine = validateRoutine(routineId);
        accessValidator.validateModificationPermission(routine);

        Long currentUserGymId = securityUtils.getCurrentUserGymId();
        User currentUser = securityUtils.getCurrentUser();
        validateExercises(request, currentUserGymId, currentUser);

        List<TrainingDay> mappedDays = routineMapper.toDomainDays(request.days());

        routine.update(request.name(), request.trainerId(), mappedDays);

        Routine updatedRoutine = routineRepository.save(routine);

        return routineMapper.toResponse(updatedRoutine, "Routine updated successfully");
    }

    private Routine validateRoutine(Long routineId) {
        return routineRepository.findById(routineId).orElseThrow(
                () -> new RoutineNotFoundException(
                        "The routine with id " + routineId + " was not found"));
    }

    private void validateExercises(UpdateRoutineRequest request, Long gymId, User currentUser) {
        request.days().forEach(day -> {
            day.exercises().forEach(exReq -> {
                Exercise exercise = exerciseRepository.findById(exReq.exerciseId())
                        .orElseThrow(() -> new ExerciseNotFoundException(
                                "Cannot update routine: Exercise with ID " + exReq.exerciseId() + " does not exist."
                        ));

                // Solo se permiten ejercicios de nuestro GYM.
                if (currentUser.getRole() != Role.SUPER_ADMIN) {
                    if (!exercise.getIsBase() && !exercise.getGymId().equals(gymId)) {
                        throw new AccessDeniedException(
                                "El ejercicio '" + exercise.getName() + "' no pertenece a tu gimnasio."
                        );
                    }
                }
            });
        });
    }
}