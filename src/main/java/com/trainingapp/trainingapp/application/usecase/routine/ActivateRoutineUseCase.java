package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.routine.ActivateRoutineRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ActivateRoutineUseCase {

    private final RoutineRepository routineRepository;
    private final SecurityUtils securityUtils;

    public ActivateRoutineUseCase(RoutineRepository routineRepository, SecurityUtils securityUtils) {
        this.routineRepository = routineRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public void execute(Long id, ActivateRoutineRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        Routine routine = findRoutineOrThrow(id);

        routine.activate(currentUser.getId(), request.startDate(), request.endDate());

        routineRepository.save(routine);
    }

    private Routine findRoutineOrThrow(Long id) {
        return routineRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Routine with id " + id + " not found."));
    }
}