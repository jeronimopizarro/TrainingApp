package com.trainingapp.trainingapp.application.usecase.exercise;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseNotFoundException;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.exercise.MuscleGroupRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.exercise.ExerciseResponse;
import com.trainingapp.trainingapp.web.dto.exercise.UpdateExerciseRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class UpdateExerciseUseCase {

    private final ExerciseRepository exerciseRepository;
    private final MuscleGroupRepository muscleGroupRepository;
    private final SecurityUtils securityUtils;

    public UpdateExerciseUseCase(ExerciseRepository exerciseRepository,
                                 MuscleGroupRepository muscleGroupRepository,
                                 SecurityUtils securityUtils) {
        this.exerciseRepository = exerciseRepository;
        this.muscleGroupRepository = muscleGroupRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public ExerciseResponse execute(Long id, UpdateExerciseRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        Exercise exercise = findExerciseOrThrow(id);

        validateOwnership(currentUser, exercise);

        validateMuscleGroupsExist(request);

        updateExerciseData(exercise, request, currentUser);

        exerciseRepository.save(exercise);

        return new ExerciseResponse(exercise.getId(), "Exercise updated successfully");
    }

    private Exercise findExerciseOrThrow(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException(
                        "The exercise with id " + id + " was not found."));
    }

    private void validateOwnership(User currentUser, Exercise exercise) {
        if (currentUser.getRole() == Role.SUPER_ADMIN) return;

        // Solo SuperAdmin puede edita un ejercicio global
        if (exercise.getIsBase()) {
            throw new AccessDeniedException("No puedes modificar ejercicios base del sistema.");
        }

        Long userGymId = extractGymId(currentUser);
        if (!exercise.getGymId().equals(userGymId)) {
            throw new AccessDeniedException("No puedes modificar ejercicios de otro gimnasio.");
        }

        // REGLA 3: El Trainer solo modifica sus propios ejercicios (El Admin puede todos los de su gym)
        if (currentUser.getRole() == Role.TRAINER) {
            boolean isCreator = exercise.getCreatedByUserId() != null
                    && exercise.getCreatedByUserId().equals(currentUser.getId());

            if (!isCreator) {
                throw new AccessDeniedException("Solo puedes modificar los ejercicios creados por ti.");
            }
        }
    }

    private Long extractGymId(User user) {
        if (user instanceof Admin admin) return admin.getGymId();
        if (user instanceof Trainer trainer) return trainer.getGymId();
        return null;
    }

    private void validateMuscleGroupsExist(UpdateExerciseRequest request) {
        request.muscleGroups().forEach(mgRequest -> {
            muscleGroupRepository.findById(mgRequest.muscleGroupId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Muscle group with ID " + mgRequest.muscleGroupId() + " does not exist."));
        });
    }

    private void updateExerciseData(Exercise exercise, UpdateExerciseRequest request, User currentUser) {
        exercise.setName(request.name());
        exercise.setDescription(request.description());
        exercise.setImageUrl(request.imageUrl());
        exercise.setVideoUrl(request.videoUrl());

        if (request.isBase() != null && currentUser.getRole() == Role.SUPER_ADMIN) {
            exercise.setIsBase(request.isBase());
        }

        // Actualización de la relación n-n
        exercise.clearMuscleGroups();
        request.muscleGroups().forEach(mgRequest ->
                exercise.addMuscleGroup(mgRequest.muscleGroupId(), mgRequest.isPrimary())
        );
    }
}