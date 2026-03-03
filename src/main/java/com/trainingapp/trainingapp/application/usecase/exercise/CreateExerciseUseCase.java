package com.trainingapp.trainingapp.application.usecase.exercise;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.exercise.MuscleGroupRepository;
import com.trainingapp.trainingapp.web.dto.exercise.CreateExerciseRequest;
import com.trainingapp.trainingapp.web.dto.exercise.ExerciseResponse;
import org.springframework.stereotype.Service;

@Service
public class CreateExerciseUseCase {

    private final ExerciseRepository exerciseRepository;
    private final MuscleGroupRepository muscleGroupRepository;

    public CreateExerciseUseCase(ExerciseRepository exerciseRepository,
                                 MuscleGroupRepository muscleGroupRepository) {
        this.exerciseRepository = exerciseRepository;
        this.muscleGroupRepository = muscleGroupRepository;
    }

    public ExerciseResponse execute(CreateExerciseRequest request) {
        request.muscleGroups().forEach(mgRequest -> {
            muscleGroupRepository.findById(mgRequest.muscleGroupId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Muscle group with ID " + mgRequest.muscleGroupId() + " does not exist."));
        });

        Exercise exercise = new Exercise(
                request.name(),
                request.description(),
                request.imageUrl(),
                request.videoUrl(),
                request.isBase(),
                request.creatorTrainerId()
        );

        request.muscleGroups().forEach(mgRequest ->
                exercise.addMuscleGroup(mgRequest.muscleGroupId(), mgRequest.isPrimary())
        );

        Exercise savedExercise = exerciseRepository.save(exercise);
        return new ExerciseResponse(savedExercise.getId(), "Exercise created successfully");
    }
}