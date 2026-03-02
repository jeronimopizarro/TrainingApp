package com.trainingapp.trainingapp.application.usecase;

import com.trainingapp.trainingapp.domain.entity.Routine;
import com.trainingapp.trainingapp.domain.exception.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.CreateRoutineResponse;
import com.trainingapp.trainingapp.web.dto.UpdateRoutineRequest;
import org.springframework.stereotype.Service;

@Service
public class UpdateRoutineUseCase {

    private final RoutineRepository routineRepository;

    public UpdateRoutineUseCase(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    public CreateRoutineResponse execute(Long routineId,
                                         UpdateRoutineRequest request) {

        Routine routine = routineRepository.findById(routineId).orElseThrow(
                () -> new RoutineNotFoundException(
                        "The routine with id " + routineId + " was not found"));

        routine.update(request);

        Routine updatedRoutine = routineRepository.save(routine);

        return new CreateRoutineResponse(updatedRoutine.getId(), "Routine updated successfully");
    }
}