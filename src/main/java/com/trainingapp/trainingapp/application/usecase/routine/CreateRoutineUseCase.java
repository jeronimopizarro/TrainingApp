package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.routine.TrainingDay;
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

    public CreateRoutineUseCase(RoutineRepository routineRepository,
                                ExerciseRepository exerciseRepository,
                                SecurityUtils securityUtils, RoutineAccessValidator accessValidator) {
        this.routineRepository = routineRepository;
        this.exerciseRepository = exerciseRepository;
        this.securityUtils = securityUtils;
        this.accessValidator = accessValidator;
    }

    @Transactional
    public CreateRoutineResponse execute(CreateRoutineRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        Long creatorGymId = securityUtils.getCurrentUserGymId();

        accessValidator.validateTargetMemberAccess(request.memberId());

        validateExercises(request, creatorGymId, currentUser);

        Routine routine = createRoutineEntity(request, currentUser.getId(), creatorGymId);

        addTrainingStructure(request, routine);

        Routine savedRoutine = routineRepository.save(routine);

        return new CreateRoutineResponse(savedRoutine.getId(),
                "Routine created successfully with all days and exercises");
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

    private Routine createRoutineEntity(CreateRoutineRequest request, Long creatorId, Long gymId) {
        return new Routine(request.name(), request.memberId(), request.trainerId(), creatorId, gymId);
    }

    private static void addTrainingStructure(CreateRoutineRequest request, Routine routine) {
        request.days().forEach(dayRequest -> {
            TrainingDay createdDay = routine.addDay(dayRequest.dayName());

            dayRequest.exercises().forEach(exerciseReq -> {
                createdDay.addDetails(
                        exerciseReq.exerciseId(),
                        exerciseReq.sets(),
                        exerciseReq.repsMin(),
                        exerciseReq.repsMax(),
                        exerciseReq.targetRIR(),
                        exerciseReq.suggestedWeight(),
                        exerciseReq.notes()
                );
            });
        });
    }
}