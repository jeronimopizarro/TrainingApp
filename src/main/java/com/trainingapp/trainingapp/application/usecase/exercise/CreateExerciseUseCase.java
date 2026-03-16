package com.trainingapp.trainingapp.application.usecase.exercise;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.exercise.MuscleGroupRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.exercise.ExerciseMapper;
import com.trainingapp.trainingapp.web.dto.exercise.CreateExerciseRequest;
import com.trainingapp.trainingapp.web.dto.exercise.ExerciseResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateExerciseUseCase {

    private final ExerciseRepository exerciseRepository;
    private final MuscleGroupRepository muscleGroupRepository;
    private final SecurityUtils securityUtils;
    private final ExerciseMapper exerciseMapper;

    public CreateExerciseUseCase(ExerciseRepository exerciseRepository,
                                 MuscleGroupRepository muscleGroupRepository,
                                 SecurityUtils securityUtils, ExerciseMapper exerciseMapper) {
        this.exerciseRepository = exerciseRepository;
        this.muscleGroupRepository = muscleGroupRepository;
        this.securityUtils = securityUtils;
        this.exerciseMapper = exerciseMapper;
    }

    @Transactional
    public ExerciseResponse execute(CreateExerciseRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        validateMuscleGroupExist(request);

        boolean isBase = (currentUser.getRole() == Role.SUPER_ADMIN) &&
                (request.isBase() != null && request.isBase());
        Long gymId = isBase ? null : securityUtils.getCurrentUserGymId();

        validateExerciseNameIsUnique(request.name(), isBase, gymId);

        Exercise exercise = exerciseMapper.toDomain(request, isBase, gymId, currentUser.getId());

        Exercise savedExercise = exerciseRepository.save(exercise);
        return exerciseMapper.toResponse(savedExercise);
    }

    private void validateMuscleGroupExist(CreateExerciseRequest request) {
        request.muscleGroups().forEach(mgRequest -> {
            muscleGroupRepository.findById(mgRequest.muscleGroupId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Muscle group with ID " + mgRequest.muscleGroupId() + " does not exist."));
        });
    }

    private void validateExerciseNameIsUnique(String name, boolean isBase, Long gymId) {
        if (isBase) {
            if (exerciseRepository.existsBaseExerciseByName(name)) {
                throw new IllegalArgumentException(
                        "Ya existe un ejercicio base con el nombre: " + name);
            }
        } else {
            if (exerciseRepository.existsByNameAndGymId(name, gymId)) {
                throw new IllegalArgumentException(
                        "Ya existe un ejercicio con ese nombre en tu gimnasio.");
            }
        }
    }
}