package com.trainingapp.trainingapp.application.usecase.exercise;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
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
        Exercise exercise = findExerciseOrThrow(id);

        validateOwnership(exercise);

        validateMuscleGroupsExist(request);

        updateExerciseData(exercise, request);

        exerciseRepository.save(exercise);

        return new ExerciseResponse(exercise.getId(), "Exercise updated successfully");
    }

    private Exercise findExerciseOrThrow(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException(
                        "The exercise with id " + id + " was not found."));
    }

    private void validateOwnership(Exercise exercise) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRole() == Role.TRAINER) {
            boolean isCreator = exercise.getCreatedByUserId() != null
                    && exercise.getCreatedByUserId().equals(currentUser.getId());

            if (!isCreator) {
                throw new AccessDeniedException(
                        "You don't have permission. You can only modify the exercises you create.");
            }
        }
    }

    private void validateMuscleGroupsExist(UpdateExerciseRequest request) {
        request.muscleGroups().forEach(mgRequest -> {
            muscleGroupRepository.findById(mgRequest.muscleGroupId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Muscle group with ID " + mgRequest.muscleGroupId() + " does not exist."));
        });
    }

    private void updateExerciseData(Exercise exercise, UpdateExerciseRequest request) {
        exercise.setName(request.name());
        exercise.setDescription(request.description());
        exercise.setImageUrl(request.imageUrl());
        exercise.setVideoUrl(request.videoUrl());

        if (request.isBase() != null) {
            exercise.setIsBase(request.isBase());
        }

        // Actualización de la relación n-n
        exercise.clearMuscleGroups();
        request.muscleGroups().forEach(mgRequest ->
                exercise.addMuscleGroup(mgRequest.muscleGroupId(), mgRequest.isPrimary())
        );
    }
}