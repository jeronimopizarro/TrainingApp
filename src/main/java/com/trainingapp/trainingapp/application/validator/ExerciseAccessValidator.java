package com.trainingapp.trainingapp.application.validator;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import org.springframework.security.access.AccessDeniedException;
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
            throw new AccessDeniedException("No tienes permisos para modificar o eliminar ejercicios base del sistema.");
        }

        securityUtils.validateSameGym(exercise.getGymId());

        if (currentUser.isTrainer()) {
            boolean isCreator = exercise.getCreatedByUserId() != null
                    && exercise.getCreatedByUserId().equals(currentUser.getId());

            if (!isCreator) {
                throw new AccessDeniedException("Solo puedes modificar o eliminar los ejercicios creados por ti.");
            }
        }
    }
}