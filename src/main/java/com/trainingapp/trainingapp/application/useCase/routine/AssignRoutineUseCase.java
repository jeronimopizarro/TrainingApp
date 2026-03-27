package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.application.mapper.routine.RoutineDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.application.validator.MemberAccessValidator;
import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineRequestStatus;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRequestRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.routine.AssignRoutineRequest;
import com.trainingapp.trainingapp.web.dto.routine.CreateRoutineResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssignRoutineUseCase {

    private final SecurityUtils securityUtils;
    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;
    private final RoutineRequestRepository routineRequestRepository;
    private final RoutineDTOMapper routineDTOMapper;
    private final MemberAccessValidator accessValidator;
    private final GymValidator gymValidator;

    public AssignRoutineUseCase(SecurityUtils securityUtils,
                                RoutineRepository routineRepository,
                                ExerciseRepository exerciseRepository,
                                RoutineRequestRepository routineRequestRepository,
                                RoutineDTOMapper routineDTOMapper,
                                MemberAccessValidator accessValidator, GymValidator gymValidator) {
        this.securityUtils = securityUtils;
        this.routineRepository = routineRepository;
        this.exerciseRepository = exerciseRepository;
        this.routineRequestRepository = routineRequestRepository;
        this.routineDTOMapper = routineDTOMapper;
        this.accessValidator = accessValidator;
        this.gymValidator = gymValidator;
    }

    @Transactional
    public CreateRoutineResponse execute(AssignRoutineRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        Long creatorId = currentUser.getId();
        Long creatorGymId = securityUtils.getCurrentUserGymId();

        gymValidator.validateExists(creatorGymId);
        accessValidator.findMemberAndValidateAccess(request.memberId());
        validateExercises(request, creatorGymId, currentUser);

        Routine routine = routineDTOMapper.toDomain(request, creatorId, creatorGymId);
        Routine savedRoutine = routineRepository.save(routine);

        completePendingRoutineRequest(request.memberId());

        return routineDTOMapper.toResponse(savedRoutine, "Rutina asignada con éxito.");
    }

    private void validateExercises(AssignRoutineRequest request, Long gymId, User currentUser) {
        request.days().forEach(day -> {
            day.exercises().forEach(exReq -> {
                Exercise exercise = exerciseRepository.findById(exReq.exerciseId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Cannot create routine: Exercise with ID " + exReq.exerciseId() + " does not exist."
                        ));

                if (!currentUser.isSuperAdmin() && !exercise.getIsBase() && !exercise.getGymId().equals(gymId)) {
                    throw new AccessDeniedException(
                            "El ejercicio '" + exercise.getName() + "' no pertenece a tu gimnasio."
                    );
                }
            });
        });
    }

    private void completePendingRoutineRequest(Long memberId) {
        routineRequestRepository.findFirstByMemberIdAndStatus(memberId, RoutineRequestStatus.PENDING)
                .ifPresent(pendingRequest -> {
                    pendingRequest.complete();
                    routineRequestRepository.save(pendingRequest);
                });
    }
}