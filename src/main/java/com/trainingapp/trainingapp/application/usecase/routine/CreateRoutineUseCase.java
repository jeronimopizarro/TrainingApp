package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.routine.TrainingDay;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseNotFoundException;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.routine.CreateRoutineRequest;
import com.trainingapp.trainingapp.web.dto.routine.CreateRoutineResponse;
import org.springframework.stereotype.Service;

@Service
public class CreateRoutineUseCase {

    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;

    public CreateRoutineUseCase(RoutineRepository routineRepository,
                                ExerciseRepository exerciseRepository) {
        this.routineRepository = routineRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public CreateRoutineResponse execute(CreateRoutineRequest request) {
        validateExercises(request);

        Routine routine = new Routine(request.name(), request.memberId(), request.trainerId(), request.createdByUserId());

            request.days().forEach(dayRequest -> {
                TrainingDay createdDay = routine.addDay(dayRequest.dayName());

                dayRequest.exercises().forEach(exerciseReq -> {
                    createdDay.addDetails(
                            exerciseReq.exerciseId(),
                            exerciseReq.sets(),
                            exerciseReq.repsMin(),
                            exerciseReq.repsMax(),
                            exerciseReq.targetRIR(),
                            exerciseReq.suggestedWeight(),
                            exerciseReq.notes()
                    );
                });
            });

            Routine savedRoutine = routineRepository.save(routine);

        return new CreateRoutineResponse(savedRoutine.getId(), "Routine created successfully with all days and exercises");
    }


    private void validateExercises(CreateRoutineRequest request) {
        request.days().forEach(day -> {
            day.exercises().forEach(exReq -> {
                exerciseRepository.findById(exReq.exerciseId())
                        .orElseThrow(() -> new ExerciseNotFoundException(
                                "Cannot create routine: Exercise with ID " + exReq.exerciseId() + " does not exist in the catalog."
                        ));
            });
        });
    }
}