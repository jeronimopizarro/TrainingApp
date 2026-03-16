package com.trainingapp.trainingapp.application.useCase.exercise;

import com.trainingapp.trainingapp.application.mapper.exercise.ExerciseDTOMapper;
import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseNotFoundException;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.exercise.ExerciseDetailResponse;
import org.springframework.stereotype.Service;

@Service
public class GetExerciseByIdUseCase {

    private final ExerciseRepository exerciseRepository;
    private final SecurityUtils securityUtils;
    private final ExerciseDTOMapper exerciseDTOMapper;

    public GetExerciseByIdUseCase(ExerciseRepository exerciseRepository,
                                  SecurityUtils securityUtils, ExerciseDTOMapper exerciseDTOMapper) {
        this.exerciseRepository = exerciseRepository;
        this.securityUtils = securityUtils;
        this.exerciseDTOMapper = exerciseDTOMapper;
    }

    public ExerciseDetailResponse execute(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        Exercise exercise = findExerciseOrThrow(id);

        validateReadPermission(currentUser, exercise);

        return exerciseDTOMapper.toDetailResponse(exercise);
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
}