package com.trainingapp.trainingapp.application.usecase;

import com.trainingapp.trainingapp.domain.entity.Routine;
import com.trainingapp.trainingapp.domain.entity.RoutineDetail;
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

    public CreateRoutineResponse execute(CreateRoutineRequest request) {
        // 1. Creamos la Rutina base (El Aggregate Root)
        Routine routine = new Routine(request.name(), request.memberId(), request.trainerId());

            // 2. Iteramos sobre los Días del JSON
            request.days().forEach(dayRequest -> {

                // Le pedimos a la rutina que cree el día. (Gracias al return, lo capturamos en una variable)
                TrainingDay createdDay = routine.addDay(dayRequest.dayName());

                // 3. Iteramos sobre los Ejercicios de ese día
                dayRequest.exercises().forEach(exerciseReq -> {

                    // Le pedimos al día que agregue sus detalles
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

            // 4. Guardamos todo el árbol en cascada
            Routine savedRoutine = routineRepository.save(routine);

        return new CreateRoutineResponse(savedRoutine.getId(), "Routine created successfully with all days and exercises");
    }
}
