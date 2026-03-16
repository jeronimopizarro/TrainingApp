package com.trainingapp.trainingapp.application.mapper.exercise;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.web.dto.exercise.CreateExerciseRequest;
import com.trainingapp.trainingapp.web.dto.exercise.ExerciseDetailResponse;
import com.trainingapp.trainingapp.web.dto.exercise.ExerciseResponse;
import org.springframework.stereotype.Component;

@Component
public class ExerciseDTOMapper {

    public Exercise toDomain(CreateExerciseRequest request, boolean isBase, Long gymId, Long userId) {
        if (request == null) return null;

        Exercise exercise = new Exercise(
                request.name(),
                request.description(),
                request.imageUrl(),
                request.videoUrl(),
                isBase,
                userId,
                gymId
        );

        if (request.muscleGroups() != null) {
            request.muscleGroups().forEach(mgRequest ->
                    exercise.addMuscleGroup(mgRequest.muscleGroupId(), mgRequest.isPrimary())
            );
        }

        return exercise;
    }

    public ExerciseResponse toResponse(Exercise exercise) {
        if (exercise == null) return null;

        return new ExerciseResponse(exercise.getId(), "Exercise created successfully");
    }

    public ExerciseDetailResponse toDetailResponse(Exercise exercise) {
        if (exercise == null) return null;

        var muscleGroups = exercise.getMuscleGroups().stream()
                .map(mg -> new ExerciseDetailResponse.MuscleGroupDetail(
                        mg.getMuscleGroupId(),
                        mg.isPrimary()
                )).toList();

        return new ExerciseDetailResponse(
                exercise.getId(), exercise.getName(), exercise.getDescription(),
                exercise.getImageUrl(), exercise.getVideoUrl(), exercise.getIsBase(),
                exercise.getCreatedByUserId(), muscleGroups
        );
    }
}