package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.domain.entity.routine.RoutineSummary;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetBaseRoutinesUseCase {

    private final RoutineRepository routineRepository;
    private final SecurityUtils securityUtils;

    public GetBaseRoutinesUseCase(RoutineRepository routineRepository, SecurityUtils securityUtils) {
        this.routineRepository = routineRepository;
        this.securityUtils = securityUtils;
    }

    public List<RoutineSummary> execute() {
        Long gymId = securityUtils.getCurrentUserGymId();
        return routineRepository.findAllBaseRoutinesByGymId(gymId);
    }
}
