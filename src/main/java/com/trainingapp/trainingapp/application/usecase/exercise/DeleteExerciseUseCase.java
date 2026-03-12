package com.trainingapp.trainingapp.application.usecase.exercise;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseNotFoundException;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class DeleteExerciseUseCase {

    private final ExerciseRepository exerciseRepository;
    private final SecurityUtils securityUtils;

    public DeleteExerciseUseCase(ExerciseRepository exerciseRepository, SecurityUtils securityUtils) {
        this.exerciseRepository = exerciseRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public void execute(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        Exercise exercise = findExerciseOrThrow(id);

        validateDeletePermission(currentUser, exercise);

        exerciseRepository.delete(exercise);
    }

    private Exercise findExerciseOrThrow(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException(
                        "The exercise with id " + id + " was not found."));
    }

    private void validateDeletePermission(User user, Exercise exercise) {
        if (user.getRole() == Role.SUPER_ADMIN) return;

        if (exercise.getIsBase()) {
            throw new AccessDeniedException("No se pueden eliminar ejercicios base del sistema.");
        }

        securityUtils.validateSameGym(exercise.getGymId());

        if (user.getRole() == Role.TRAINER) {
            boolean isCreator = exercise.getCreatedByUserId() != null
                    && exercise.getCreatedByUserId().equals(user.getId());

            if (!isCreator) {
                throw new AccessDeniedException("Solo puedes eliminar los ejercicios que tú creaste.");
            }
        }
    }
}