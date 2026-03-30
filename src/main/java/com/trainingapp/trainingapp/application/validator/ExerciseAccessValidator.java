package com.trainingapp.trainingapp.application.validator;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.exception.exercise.UnauthorizedBaseExerciseModificationException;
import com.trainingapp.trainingapp.domain.exception.exercise.UnauthorizedExerciseModificationException;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import org.springframework.stereotype.Component;

@Component
public class ExerciseAccessValidator {

    private final SecurityUtils securityUtils;

    public ExerciseAccessValidator(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    public void validateWriteAccess(Exercise exercise) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.isSuperAdmin()) return;

        // Ejercicios Base: Solo SuperAdmin puede tocarlos
        if (exercise.getIsBase()) {
            throw new UnauthorizedBaseExerciseModificationException();
        }

        securityUtils.validateSameGym(exercise.getGymId());

        if (currentUser.isTrainer()) {
            boolean isCreator = exercise.getCreatedByUserId() != null
                    && exercise.getCreatedByUserId().equals(currentUser.getId());

            if (!isCreator) {
                throw new UnauthorizedExerciseModificationException();
            }
        }
    }
}