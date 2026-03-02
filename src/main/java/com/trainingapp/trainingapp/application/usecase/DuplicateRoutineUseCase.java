package com.trainingapp.trainingapp.application.usecase;

import com.trainingapp.trainingapp.domain.entity.Routine;
import com.trainingapp.trainingapp.domain.exception.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.CreateRoutineResponse;
import com.trainingapp.trainingapp.web.dto.DuplicateRoutineRequest;
import org.springframework.stereotype.Service;

@Service
public class DuplicateRoutineUseCase {
    private final RoutineRepository routineRepository;

    public DuplicateRoutineUseCase(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    public CreateRoutineResponse execute(Long sourceRoutineId, DuplicateRoutineRequest request) {
        Routine sourceRoutine = routineRepository.findById(sourceRoutineId).orElseThrow(
                () -> new RoutineNotFoundException(
                        "The routine with id " + sourceRoutineId + " was not found"));

        Long targetTrainerId = request.trainerId() != null ? request.trainerId() : sourceRoutine.getTrainerId();

        Routine newRoutine = sourceRoutine.duplicate(request.newName(), request.targetMemberId(),
                targetTrainerId, request.createdByUserId());

        Routine savedRoutine = routineRepository.save(newRoutine);

        return new CreateRoutineResponse(savedRoutine.getId(), "Routine duplicated successfully");
    }
}