package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.routine.TrainingDay;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseNotFoundException;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.routine.CreateRoutineRequest;
import com.trainingapp.trainingapp.web.dto.routine.CreateRoutineResponse;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class CreateRoutineUseCase {

    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;
    private final SecurityUtils securityUtils;

    public CreateRoutineUseCase(RoutineRepository routineRepository,
                                ExerciseRepository exerciseRepository,
                                SecurityUtils securityUtils) {
        this.routineRepository = routineRepository;
        this.exerciseRepository = exerciseRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public CreateRoutineResponse execute(CreateRoutineRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        validateCreationPermission(currentUser, request.memberId());

        validateExercises(request);

        Routine routine = createRoutineEntity(request, currentUser.getId());

        addTrainingStructure(request, routine);

        Routine savedRoutine = routineRepository.save(routine);

        return new CreateRoutineResponse(savedRoutine.getId(),
                "Routine created successfully with all days and exercises");
    }

    private void validateCreationPermission(User currentUser, Long targetMemberId) {
        if (currentUser.getRole() == Role.MEMBER && !currentUser.getId().equals(targetMemberId)) {
            throw new AccessDeniedException(
                    "No tienes permiso para crearle una rutina a otro socio.");
        }
    }

    private void validateExercises(CreateRoutineRequest request) {
        request.days().forEach(day -> {
            day.exercises().forEach(exReq -> {
                exerciseRepository.findById(exReq.exerciseId())
                        .orElseThrow(() -> new ExerciseNotFoundException(
                                "Cannot create routine: Exercise with ID " + exReq.exerciseId() + " does not exist in the catalog."
                        ));
            });
        });
    }

    private Routine createRoutineEntity(CreateRoutineRequest request, Long creatorId) {
        return new Routine(request.name(), request.memberId(), request.trainerId(), creatorId);
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