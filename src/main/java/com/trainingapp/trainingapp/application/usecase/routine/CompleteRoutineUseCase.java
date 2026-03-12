package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.exception.routine.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CompleteRoutineUseCase {

    private final RoutineRepository routineRepository;
    private final SecurityUtils securityUtils;

    public CompleteRoutineUseCase(RoutineRepository routineRepository, SecurityUtils securityUtils) {
        this.routineRepository = routineRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public void execute(Long id){
        User currentUser = securityUtils.getCurrentUser();

        Routine routine = findRoutineOrThrow(id);

        routine.complete(currentUser.getId());

        routineRepository.save(routine);
    }

    private Routine findRoutineOrThrow(Long id) {
        return routineRepository.findById(id)
                .orElseThrow(() -> new RoutineNotFoundException(
                        "The routine with id " + id + " was not found"));
    }
}