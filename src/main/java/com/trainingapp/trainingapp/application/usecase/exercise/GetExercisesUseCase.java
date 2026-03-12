package com.trainingapp.trainingapp.application.usecase.exercise;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
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

    public GetExercisesUseCase(ExerciseRepository exerciseRepository,
                               SecurityUtils securityUtils) {
        this.exerciseRepository = exerciseRepository;
        this.securityUtils = securityUtils;
    }

    public List<ExerciseDetailResponse> execute(Long muscleGroupId) {
        User currentUser = securityUtils.getCurrentUser();

        List<Exercise> exercises = fetchAllowedExercises(currentUser, muscleGroupId);

        return exercises.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private List<Exercise> fetchAllowedExercises(User currentUser, Long muscleGroupId) {
        if (currentUser.getRole() == Role.SUPER_ADMIN) {
            // SuperAdmin ve todo, con o sin filtro de músculo
            return (muscleGroupId != null)
                    ? exerciseRepository.findByMuscleGroupId(muscleGroupId)
                    : exerciseRepository.findAll();
        }

        Long userGymId = securityUtils.getCurrentUserGymId();

        return exerciseRepository.findAllowedForGym(userGymId, muscleGroupId);
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
