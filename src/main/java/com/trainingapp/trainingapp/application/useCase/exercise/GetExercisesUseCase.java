package com.trainingapp.trainingapp.application.useCase.exercise;

import com.trainingapp.trainingapp.application.mapper.exercise.ExerciseDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.exercise.ExerciseDetailResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetExercisesUseCase {

    private final ExerciseRepository exerciseRepository;
    private final SecurityUtils securityUtils;
    private final ExerciseDTOMapper exerciseDTOMapper;
    private final GymValidator gymValidator;

    public GetExercisesUseCase(ExerciseRepository exerciseRepository,
                               SecurityUtils securityUtils, ExerciseDTOMapper exerciseDTOMapper,
                               GymValidator gymValidator) {
        this.exerciseRepository = exerciseRepository;
        this.securityUtils = securityUtils;
        this.exerciseDTOMapper = exerciseDTOMapper;
        this.gymValidator = gymValidator;
    }

    public List<ExerciseDetailResponse> execute(Long muscleGroupId) {
        User currentUser = securityUtils.getCurrentUser();

        List<Exercise> exercises = fetchAllowedExercises(currentUser, muscleGroupId);

        return exercises.stream()
                .map(exerciseDTOMapper::toDetailResponse)
                .toList();
    }

    private List<Exercise> fetchAllowedExercises(User currentUser, Long muscleGroupId) {
        if (currentUser.isSuperAdmin()) {
            // SuperAdmin ve todo, con o sin filtro de músculo
            return (muscleGroupId != null)
                    ? exerciseRepository.findByMuscleGroupId(muscleGroupId)
                    : exerciseRepository.findAll();
        }

        Long userGymId = securityUtils.getCurrentUserGymId();
        gymValidator.validateExists(userGymId);

        return exerciseRepository.findAllowedForGym(userGymId, muscleGroupId);
    }
}