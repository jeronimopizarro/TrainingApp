package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.routine.ActivateRoutineRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class ActivateRoutineUseCase {

    private final RoutineRepository routineRepository;
    private final SecurityUtils securityUtils;
    private final RoutineAccessValidator accessValidator;

    public ActivateRoutineUseCase(RoutineRepository routineRepository, SecurityUtils securityUtils,
                                  RoutineAccessValidator accessValidator) {
        this.routineRepository = routineRepository;
        this.securityUtils = securityUtils;
        this.accessValidator = accessValidator;
    }

    @Transactional
    public void execute(Long id, ActivateRoutineRequest request) {
        Routine routine = findRoutineOrThrow(id);

        accessValidator.validateModificationPermission(routine);

        routine.activate(request.startDate(), request.endDate());

        routineRepository.save(routine);
    }


    private Routine findRoutineOrThrow(Long id) {
        return routineRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Routine with id " + id + " not found."));
    }
}