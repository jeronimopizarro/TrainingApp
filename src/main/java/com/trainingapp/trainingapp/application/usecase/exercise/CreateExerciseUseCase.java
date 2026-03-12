package com.trainingapp.trainingapp.application.usecase.exercise;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.exercise.MuscleGroupRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.exercise.CreateExerciseRequest;
import com.trainingapp.trainingapp.web.dto.exercise.ExerciseResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateExerciseUseCase {

    private final ExerciseRepository exerciseRepository;
    private final MuscleGroupRepository muscleGroupRepository;
    private final SecurityUtils securityUtils;

    public CreateExerciseUseCase(ExerciseRepository exerciseRepository,
                                 MuscleGroupRepository muscleGroupRepository,
                                 SecurityUtils securityUtils) {
        this.exerciseRepository = exerciseRepository;
        this.muscleGroupRepository = muscleGroupRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public ExerciseResponse execute(CreateExerciseRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        validateMuscleGroupExist(request);

        Exercise exercise = buildExerciseEntity(request, currentUser);

        addMuscleGroups(request, exercise);

        Exercise savedExercise = exerciseRepository.save(exercise);

        return new ExerciseResponse(savedExercise.getId(), "Exercise created successfully");
    }

    private void validateMuscleGroupExist(CreateExerciseRequest request) {
        request.muscleGroups().forEach(mgRequest -> {
            muscleGroupRepository.findById(mgRequest.muscleGroupId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Muscle group with ID " + mgRequest.muscleGroupId() + " does not exist."));
        });
    }

    private Exercise buildExerciseEntity(CreateExerciseRequest request, User user) {
        boolean isBase = (user.getRole() == Role.SUPER_ADMIN) && (request.isBase() != null && request.isBase());

        Long gymId = isBase ? null : securityUtils.getCurrentUserGymId();

        return new Exercise(
                request.name(),
                request.description(),
                request.imageUrl(),
                request.videoUrl(),
                isBase,
                user.getId(),
                gymId
        );
    }

    private static void addMuscleGroups(CreateExerciseRequest request, Exercise exercise) {
        request.muscleGroups().forEach(mgRequest ->
                exercise.addMuscleGroup(mgRequest.muscleGroupId(), mgRequest.isPrimary())
        );
    }
}