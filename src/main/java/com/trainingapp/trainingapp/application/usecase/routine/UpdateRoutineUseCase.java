package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.routine.RoutineDetail;
import com.trainingapp.trainingapp.domain.entity.routine.TrainingDay;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseNotFoundException;
import com.trainingapp.trainingapp.domain.exception.routine.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.routine.CreateRoutineResponse;
import com.trainingapp.trainingapp.web.dto.routine.UpdateRoutineRequest;
import com.trainingapp.trainingapp.web.dto.routine.UpdateRoutineRequest.UpdateTrainingDayRequest;
import com.trainingapp.trainingapp.web.dto.routine.UpdateRoutineRequest.UpdateRoutineDetailRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UpdateRoutineUseCase {

    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;

    public UpdateRoutineUseCase(RoutineRepository routineRepository,
                                ExerciseRepository exerciseRepository) {
        this.routineRepository = routineRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @Transactional
    public CreateRoutineResponse execute(Long routineId,
                                         UpdateRoutineRequest request) {

        Routine routine = validateRoutine(routineId);

        validateExercises(request);

        List<TrainingDay> mappedDays = mapToDomainDays(request.days());
        routine.update(request.name(), request.trainerId(), mappedDays);

        Routine updatedRoutine = routineRepository.save(routine);

        return new CreateRoutineResponse(updatedRoutine.getId(), "Routine updated successfully");
    }

    private Routine validateRoutine(Long routineId) {
        return routineRepository.findById(routineId).orElseThrow(
                () -> new RoutineNotFoundException(
                        "The routine with id " + routineId + " was not found"));
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

    private List<TrainingDay> mapToDomainDays(List<UpdateTrainingDayRequest> dayRequests) {
        List<TrainingDay> domainDays = new ArrayList<>();
        int dayOrder = 1;

        for (UpdateTrainingDayRequest dayReq : dayRequests) {
            TrainingDay day = new TrainingDay(dayReq.dayName(), dayOrder++);
            day.setId(dayReq.id());

            for (UpdateRoutineDetailRequest exReq : dayReq.exercises()) {
                RoutineDetail detail = new RoutineDetail(
                        exReq.exerciseId(),
                        0, // El orden se recalcula adentro
                        exReq.sets(), exReq.repsMin(), exReq.repsMax(),
                        exReq.targetRIR(), exReq.suggestedWeight(), exReq.notes()
                );
                detail.setId(exReq.id());
                day.getDetails().add(detail);
            }
            domainDays.add(day);
        }
        return domainDays;
    }
}