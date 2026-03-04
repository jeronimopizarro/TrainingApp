package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseNotFoundException;
import com.trainingapp.trainingapp.domain.exception.routine.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.routine.CreateRoutineResponse;
import com.trainingapp.trainingapp.web.dto.routine.UpdateRoutineRequest;
import org.springframework.stereotype.Service;

@Service
public class UpdateRoutineUseCase {

    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;

    public UpdateRoutineUseCase(RoutineRepository routineRepository,
                                ExerciseRepository exerciseRepository) {
        this.routineRepository = routineRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public CreateRoutineResponse execute(Long routineId,
                                         UpdateRoutineRequest request) {

        Routine routine = validateRoutine(routineId);

        validateExercises(request);

        routine.update(request);

        Routine updatedRoutine = routineRepository.save(routine);

        return new CreateRoutineResponse(updatedRoutine.getId(), "Routine updated successfully");
    }

    private Routine validateRoutine(Long routineId) {
        Routine routine = routineRepository.findById(routineId).orElseThrow(
                () -> new RoutineNotFoundException(
                        "The routine with id " + routineId + " was not found"));
        return routine;
    }

    private void validateExercises(UpdateRoutineRequest request) {
        request.days().forEach(day -> {
            day.exercises().forEach(exReq -> {
                exerciseRepository.findById(exReq.exerciseId())
                        .orElseThrow(() -> new ExerciseNotFoundException(
                                "Cannot update routine: Exercise with ID " + exReq.exerciseId() + " does not exist in the catalog."
                        ));
            });
        });
    }
}