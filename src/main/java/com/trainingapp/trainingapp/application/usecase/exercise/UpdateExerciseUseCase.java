package com.trainingapp.trainingapp.application.usecase.exercise;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseNotFoundException;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.exercise.MuscleGroupRepository;
import com.trainingapp.trainingapp.web.dto.exercise.ExerciseResponse;
import com.trainingapp.trainingapp.web.dto.exercise.UpdateExerciseRequest;
import org.springframework.stereotype.Service;

@Service
public class UpdateExerciseUseCase {

    private final ExerciseRepository exerciseRepository;
    private final MuscleGroupRepository muscleGroupRepository;

    public UpdateExerciseUseCase(ExerciseRepository exerciseRepository, MuscleGroupRepository muscleGroupRepository) {
        this.exerciseRepository = exerciseRepository;
        this.muscleGroupRepository = muscleGroupRepository;
    }

    public ExerciseResponse execute(Long id, UpdateExerciseRequest request) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException("The exercise with id " + id + " was not found."));

        request.muscleGroups().forEach(mgRequest -> {
            muscleGroupRepository.findById(mgRequest.muscleGroupId())
                    .orElseThrow(() -> new IllegalArgumentException("Muscle group with ID " + mgRequest.muscleGroupId() + " does not exist."));
        });

        exercise.setName(request.name());
        exercise.setDescription(request.description());
        exercise.setImageUrl(request.imageUrl());
        exercise.setVideoUrl(request.videoUrl());
        if (request.isBase() != null) {
            exercise.setIsBase(request.isBase());
        }

        exercise.clearMuscleGroups();
        request.muscleGroups().forEach(mgRequest ->
                exercise.addMuscleGroup(mgRequest.muscleGroupId(), mgRequest.isPrimary())
        );

        exerciseRepository.save(exercise);
        return new ExerciseResponse(exercise.getId(), "Exercise updated successfully");
    }
}