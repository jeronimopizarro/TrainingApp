package com.trainingapp.trainingapp.application.usecase;

import com.trainingapp.trainingapp.domain.entity.Routine;
import com.trainingapp.trainingapp.domain.entity.TrainingDay;
import com.trainingapp.trainingapp.domain.repository.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.CreateRoutineRequest;
import com.trainingapp.trainingapp.web.dto.CreateRoutineResponse;
import org.springframework.stereotype.Service;

@Service
public class CreateRoutineUseCase {

    private final RoutineRepository routineRepository;

    public CreateRoutineUseCase(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    public CreateRoutineResponse execute(CreateRoutineRequest routineRequest) {
        Routine routine = new Routine(routineRequest.name(), routineRequest.memberId(), routineRequest.trainerId());
        routine.addDay(routineRequest.dayName());

        TrainingDay trainingDay = routine.getDays().get(routine.getDays().size() - 1);

        trainingDay.addDetails(routineRequest.exerciseId(), routineRequest.sets(), routineRequest.repsMin(),
                routineRequest.repsMax(), routineRequest.targetRIR(), routineRequest.suggestedWeight(),
                routineRequest.notes());

        Routine savedRoutine = routineRepository.save(routine);

        return new CreateRoutineResponse(savedRoutine.getId(), "Routine created successfully");
    }
}
