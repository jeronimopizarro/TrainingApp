package com.trainingapp.trainingapp.application.usecase.exercise;

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
        MuscleGroup muscleGroup = muscleGroupRepository.findById(id).orElseThrow(
                () -> new MuscleGroupNotFoundException(
                        "The muscle group with id" + id + " was not found."));

        return new MuscleGroupResponse(muscleGroup.getId(), muscleGroup.getName(),
                muscleGroup.getDescription());
    }
}