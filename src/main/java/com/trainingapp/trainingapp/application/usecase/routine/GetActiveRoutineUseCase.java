package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.routine.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.domain.repository.user.UserRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.routine.RoutineResponseMapper;
import com.trainingapp.trainingapp.web.dto.routine.RoutineResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class GetActiveRoutineUseCase {

    private final RoutineRepository routineRepository;
    private final SecurityUtils securityUtils;
    private final RoutineResponseMapper mapper;
    private final RoutineAccessValidator accessValidator;

    public GetActiveRoutineUseCase(RoutineRepository routineRepository, SecurityUtils securityUtils,
                                   RoutineResponseMapper mapper,
                                   RoutineAccessValidator accessValidator) {

        this.routineRepository = routineRepository;
        this.securityUtils = securityUtils;
        this.mapper = mapper;
        this.accessValidator = accessValidator;
    }

    public RoutineResponse execute(Long memberId) {
        accessValidator.validateTargetMemberAccess(memberId);

        Routine routine = findActiveRoutineOrThrow(memberId);

        return mapper.toDto(routine);
    }


    private Routine findActiveRoutineOrThrow(Long memberId) {
        return routineRepository.findByMemberIdAndStatus(memberId,
                RoutineStatus.ACTIVE).orElseThrow(() -> new RoutineNotFoundException(
                "No active routine found for member with ID: " + memberId));
    }
}