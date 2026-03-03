package com.trainingapp.trainingapp.application.usecase.exercise;

import com.trainingapp.trainingapp.domain.entity.exercise.MuscleGroup;
import com.trainingapp.trainingapp.domain.repository.exercise.MuscleGroupRepository;
import com.trainingapp.trainingapp.web.dto.exercise.MuscleGroupResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllMuscleGroupsUseCase {

    private final MuscleGroupRepository muscleGroupRepository;

    public GetAllMuscleGroupsUseCase(MuscleGroupRepository repository) {
        this.muscleGroupRepository = repository;
    }

    public List<MuscleGroupResponse> execute() {
        List<MuscleGroup> muscleGroups = muscleGroupRepository.findAll();

        return muscleGroups.stream().map(mg -> new MuscleGroupResponse(mg.getId(), mg.getName(),
                mg.getDescription())).toList();
    }
}