package com.trainingapp.trainingapp.application.useCase.exercise.muscleGroup;

import com.trainingapp.trainingapp.domain.entity.exercise.MuscleGroup;
import com.trainingapp.trainingapp.domain.exception.exercise.MuscleGroupNotFoundException;
import com.trainingapp.trainingapp.domain.repository.exercise.MuscleGroupRepository;
import com.trainingapp.trainingapp.web.dto.exercise.MuscleGroupResponse;
import org.springframework.stereotype.Service;

@Service
public class GetMuscleGroupByIdUseCase {

    private final MuscleGroupRepository muscleGroupRepository;

    public GetMuscleGroupByIdUseCase(MuscleGroupRepository repository) {
        this.muscleGroupRepository = repository;
    }

    public MuscleGroupResponse execute(Long id) {
        MuscleGroup muscleGroup = findMuscleGroupsOrThrow(id);

        return mapToResponse(muscleGroup);
    }

    private MuscleGroup findMuscleGroupsOrThrow(Long id) {
        return muscleGroupRepository.findById(id).orElseThrow(
                () -> new MuscleGroupNotFoundException(id));
    }

    private MuscleGroupResponse mapToResponse(MuscleGroup muscleGroup) {
        return new MuscleGroupResponse(muscleGroup.getId(), muscleGroup.getName(),
                muscleGroup.getDescription());
    }
}