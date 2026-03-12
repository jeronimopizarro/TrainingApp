package com.trainingapp.trainingapp.application.usecase.exercise;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseNotFoundException;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.exercise.ExerciseDetailResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class GetExerciseByIdUseCase {

    private final ExerciseRepository exerciseRepository;
    private final SecurityUtils securityUtils;

    public GetExerciseByIdUseCase(ExerciseRepository exerciseRepository,
                                  SecurityUtils securityUtils) {
        this.exerciseRepository = exerciseRepository;
        this.securityUtils = securityUtils;
    }

    public ExerciseDetailResponse execute(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        Exercise exercise = findExerciseOrThrow(id);

        validateReadPermission(currentUser, exercise);

        return mapToResponse(exercise);
    }

    private Exercise findExerciseOrThrow(Long id) {
        return exerciseRepository.findById(id).orElseThrow(() -> new ExerciseNotFoundException(
                "The exercise with id " + id + " was not found."));
    }

    private void validateReadPermission(User user, Exercise exercise) {
        // Los ejercicios BASE y el SUPER_ADMIN pasan directo
        if (user.getRole() == Role.SUPER_ADMIN || exercise.getIsBase()) {
            return;
        }

        securityUtils.validateSameGym(exercise.getGymId());
    }

    private ExerciseDetailResponse mapToResponse(Exercise exercise) {
        var muscleGroups = exercise.getMuscleGroups().stream()
                .map(mg -> new ExerciseDetailResponse.MuscleGroupDetail(
                        mg.getMuscleGroupId(),
                        mg.isPrimary()
                )).toList();

        return new ExerciseDetailResponse(
                exercise.getId(), exercise.getName(), exercise.getDescription(),
                exercise.getImageUrl(), exercise.getVideoUrl(), exercise.getIsBase(),
                exercise.getCreatedByUserId(), muscleGroups
        );
    }
}