package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.application.mapper.routine.RoutineDTOMapper;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.routine.CreateBaseRoutineRequest;
import com.trainingapp.trainingapp.web.dto.routine.CreateRoutineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateBaseRoutineUseCase {

    private final RoutineRepository routineRepository;
    private final SecurityUtils securityUtils;
    private final RoutineDTOMapper routineDTOMapper;

    public CreateBaseRoutineUseCase(RoutineRepository routineRepository,
                                    SecurityUtils securityUtils,
                                    RoutineDTOMapper routineDTOMapper) {
        this.routineRepository = routineRepository;
        this.securityUtils = securityUtils;
        this.routineDTOMapper = routineDTOMapper;
    }

    @Transactional
    public CreateRoutineResponse execute(CreateBaseRoutineRequest request) {
        Long trainerId = securityUtils.getCurrentUser().getId();
        Long gymId = securityUtils.getCurrentUserGymId();

        Routine routine = routineDTOMapper.toDomain(request, trainerId, gymId);
        Routine savedRoutine = routineRepository.save(routine);

        return routineDTOMapper.toResponse(savedRoutine, "Rutina base creada con éxito.");
    }
}
