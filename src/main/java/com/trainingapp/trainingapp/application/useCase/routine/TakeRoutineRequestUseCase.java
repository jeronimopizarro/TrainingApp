package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.domain.entity.routine.RoutineRequest;
import com.trainingapp.trainingapp.domain.exception.routine.RoutineRequestNotFoundException;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRequestRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TakeRoutineRequestUseCase {

    private final RoutineRequestRepository routineRequestRepository;
    private final SecurityUtils securityUtils;

    public TakeRoutineRequestUseCase(RoutineRequestRepository routineRequestRepository,
                                     SecurityUtils securityUtils) {
        this.routineRequestRepository = routineRequestRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public void execute(Long requestId) {
        Long currentTrainerId = securityUtils.getCurrentUser().getId();

        RoutineRequest request = routineRequestRepository.findById(requestId)
                .orElseThrow(() -> new RoutineRequestNotFoundException(requestId));

        securityUtils.validateSameGym(request.getGymId());

        request.assignTrainer(currentTrainerId);

        routineRequestRepository.save(request);
    }
}