package com.trainingapp.trainingapp.application.usecase;

import com.trainingapp.trainingapp.domain.entity.Routine;
import com.trainingapp.trainingapp.domain.repository.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.GetAllRoutinesByTrainerIdResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllRoutinesByTrainerIdUseCase {

    private final RoutineRepository routineRepository;

    public GetAllRoutinesByTrainerIdUseCase(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    public List<GetAllRoutinesByTrainerIdResponse> execute(Long trainerId) {
        List<Routine> routines = routineRepository.findAllByTrainerId(trainerId);

        return routines.stream().map(
                routine -> new GetAllRoutinesByTrainerIdResponse(routine.getId(),
                        routine.getName(), routine.getStatus(), routine.getMemberId())).toList();
    }
}