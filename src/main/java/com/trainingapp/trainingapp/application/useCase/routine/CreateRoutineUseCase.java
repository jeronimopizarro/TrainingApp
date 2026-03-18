package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.application.mapper.routine.RoutineDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.routine.CreateRoutineRequest;
import com.trainingapp.trainingapp.web.dto.routine.CreateRoutineResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class CreateRoutineUseCase {

    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;
    private final SecurityUtils securityUtils;
    private final RoutineAccessValidator accessValidator;
    private final RoutineDTOMapper routineDTOMapper;
    private final GymValidator gymValidator;

    public CreateRoutineUseCase(RoutineRepository routineRepository,
                                ExerciseRepository exerciseRepository,
                                SecurityUtils securityUtils, RoutineAccessValidator accessValidator,
                                RoutineDTOMapper routineDTOMapper, GymValidator gymValidator) {
        this.routineRepository = routineRepository;
        this.exerciseRepository = exerciseRepository;
        this.securityUtils = securityUtils;
        this.accessValidator = accessValidator;
        this.routineDTOMapper = routineDTOMapper;
        this.gymValidator = gymValidator;
    }

    @Transactional
    public CreateRoutineResponse execute(CreateRoutineRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        Long creatorGymId = securityUtils.getCurrentUserGymId();

        gymValidator.validateExists(creatorGymId);
        accessValidator.validateTargetMemberAccess(request.memberId());
        validateExercises(request, creatorGymId, currentUser);

        Routine routine = routineDTOMapper.toDomain(request, currentUser.getId(), creatorGymId);

        Routine savedRoutine = routineRepository.save(routine);
        return routineDTOMapper.toResponse(savedRoutine, "Routine created successfully");
    }

    private void validateExercises(CreateRoutineRequest request, Long gymId, User currentUser) {
        request.days().forEach(day -> {
            day.exercises().forEach(exReq -> {
                Exercise exercise = exerciseRepository.findById(exReq.exerciseId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Cannot create routine: Exercise with ID " + exReq.exerciseId() + " does not exist."
                        ));

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