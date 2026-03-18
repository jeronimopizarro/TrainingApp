package com.trainingapp.trainingapp.application.useCase.exercise;

import com.trainingapp.trainingapp.application.mapper.exercise.ExerciseDTOMapper;
import com.trainingapp.trainingapp.application.validator.ExerciseAccessValidator;
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
    private final ExerciseDTOMapper exerciseDTOMapper;
    private final ExerciseAccessValidator exerciseAccessValidator;

    public UpdateExerciseUseCase(ExerciseRepository exerciseRepository,
                                 MuscleGroupRepository muscleGroupRepository,
                                 SecurityUtils securityUtils, ExerciseDTOMapper exerciseDTOMapper,
                                 ExerciseAccessValidator exerciseAccessValidator) {
        this.exerciseRepository = exerciseRepository;
        this.muscleGroupRepository = muscleGroupRepository;
        this.securityUtils = securityUtils;
        this.exerciseDTOMapper = exerciseDTOMapper;
        this.exerciseAccessValidator = exerciseAccessValidator;
    }

    @Transactional
    public ExerciseResponse execute(Long id, UpdateExerciseRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        Exercise exercise = findExerciseOrThrow(id);

        exerciseAccessValidator.validateWriteAccess(exercise);
        validateExerciseNameIsUniqueForUpdate(request.name(), exercise.getIsBase(), exercise.getGymId(), id);
        validateMuscleGroupsExist(request);

        updateExerciseData(exercise, request, currentUser);

        exerciseRepository.save(exercise);
        return exerciseDTOMapper.toResponse(exercise);
    }

    private Exercise findExerciseOrThrow(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException(
                        "The exercise with id " + id + " was not found."));
    }

    private void validateExerciseNameIsUniqueForUpdate(String name, boolean isBase, Long gymId, Long currentId) {
        if (isBase) {
            if (exerciseRepository.existsBaseExerciseByNameAndIdNot(name, currentId)) {
                throw new IllegalArgumentException("Ya existe otro ejercicio base con el nombre: " + name);
            }
        } else {
            if (exerciseRepository.existsByNameAndGymIdAndIdNot(name, gymId, currentId)) {
                throw new IllegalArgumentException("Ya existe otro ejercicio con ese nombre en tu gimnasio.");
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

    private void updateExerciseData(Exercise exercise, UpdateExerciseRequest request, User currentUser) {
        exercise.updateDetails(
                request.name(),
                request.description(),
                request.imageUrl(),
                request.videoUrl()
        );

        if (request.isBase() != null && currentUser.isSuperAdmin()) {
            exercise.setIsBase(request.isBase());
        }

        exercise.clearMuscleGroups();
        if (request.muscleGroups() != null) {
            request.muscleGroups().forEach(mgRequest ->
                    exercise.addMuscleGroup(mgRequest.muscleGroupId(), mgRequest.isPrimary())
            );
        }
    }
}