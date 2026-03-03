package com.trainingapp.trainingapp.application.usecase.exercise;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseNotFoundException;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.web.dto.exercise.ExerciseDetailResponse;
import org.springframework.stereotype.Service;

@Service
public class GetExerciseByIdUseCase {

    private final ExerciseRepository exerciseRepository;

    public GetExerciseByIdUseCase(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    public ExerciseDetailResponse execute(Long id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException("The exercise with id " + id + " was not found."));

        return mapToResponse(exercise);
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
                exercise.getCreatorTrainerId(), muscleGroups
        );
    }
}