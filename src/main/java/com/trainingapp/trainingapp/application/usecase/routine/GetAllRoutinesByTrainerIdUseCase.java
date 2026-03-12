package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.domain.entity.routine.RoutineSummary;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.routine.GetAllRoutinesByTrainerIdResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllRoutinesByTrainerIdUseCase {

    private final RoutineRepository routineRepository;
    private final SecurityUtils securityUtils;

    public GetAllRoutinesByTrainerIdUseCase(RoutineRepository routineRepository,
                                            SecurityUtils securityUtils) {
        this.routineRepository = routineRepository;
        this.securityUtils = securityUtils;
    }

    public List<GetAllRoutinesByTrainerIdResponse> execute(Long trainerId) {
        validateTrainerAccess(trainerId);

        List<RoutineSummary> summaries = routineRepository.findAllSummariesByTrainerId(trainerId);

        return mapToResponse(summaries);
    }

    private void validateTrainerAccess(Long trainerId) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRole() == Role.TRAINER && !currentUser.getId().equals(trainerId)) {
            throw new AccessDeniedException("No tienes permiso para ver las rutinas de otro entrenador.");
        }
    }

    private List<GetAllRoutinesByTrainerIdResponse> mapToResponse(List<RoutineSummary> summaries) {
        return summaries.stream()
                .map(s -> new GetAllRoutinesByTrainerIdResponse(
                        s.id(),
                        s.name(),
                        s.status(),
                        s.memberId()))
                .toList();
    }
}