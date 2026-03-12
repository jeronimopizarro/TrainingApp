package com.trainingapp.trainingapp.application.usecase.exercise;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.web.dto.exercise.ExerciseDetailResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllExercisesByGroupIdUseCase {

    private final ExerciseRepository exerciseRepository;

    public GetAllExercisesByGroupIdUseCase(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    public List<ExerciseDetailResponse> execute(Long muscleGroupId) {
        List<Exercise> exercises = fetchExercises(muscleGroupId);

        return exercises.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private List<Exercise> fetchExercises(Long muscleGroupId) {
        if (muscleGroupId != null) {
            return exerciseRepository.findByMuscleGroupId(muscleGroupId);
        }
        return exerciseRepository.findAll();
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
                exercise.getCreatedByUserId(), muscleGroups
        );
    }
}
