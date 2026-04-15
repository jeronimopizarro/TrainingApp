package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.application.mapper.routine.RoutineDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineRequestStatus;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseNotFoundException;
import com.trainingapp.trainingapp.domain.exception.exercise.UnauthorizedExerciseAccessException;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRequestRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.routine.CreatePersonalRoutineRequest;
import com.trainingapp.trainingapp.web.dto.routine.CreateRoutineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class CreatePersonalRoutineUseCase {

    private final SecurityUtils securityUtils;
    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;
    private final RoutineRequestRepository routineRequestRepository;
    private final RoutineDTOMapper routineDTOMapper;
    private final GymValidator gymValidator;

    public CreatePersonalRoutineUseCase(SecurityUtils securityUtils,
                                        RoutineRepository routineRepository,
                                        ExerciseRepository exerciseRepository,
                                        RoutineRequestRepository routineRequestRepository,
                                        RoutineDTOMapper routineDTOMapper,
                                        GymValidator gymValidator) {
        this.securityUtils = securityUtils;
        this.routineRepository = routineRepository;
        this.exerciseRepository = exerciseRepository;
        this.routineRequestRepository = routineRequestRepository;
        this.routineDTOMapper = routineDTOMapper;
        this.gymValidator = gymValidator;
    }

    @Transactional
    public CreateRoutineResponse execute(CreatePersonalRoutineRequest request) {
        Long memberId = securityUtils.getCurrentUser().getId();
        Long gymId = securityUtils.getCurrentUserGymId();

        gymValidator.validateExists(gymId);
        validateExercises(request, gymId);

        // Deactivamos cualquier rutina previa que esté ACTIVE
        routineRepository.findByMemberIdAndStatus(memberId, com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus.ACTIVE)
                .ifPresent(oldRoutine -> {
                    oldRoutine.inactive();
                    routineRepository.save(oldRoutine);
                });

        Routine routine = routineDTOMapper.toDomain(request, memberId, gymId);
        
        // Activamos la nueva rutina inmediatamente
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = request.durationMonths() != null 
                ? startDate.plusMonths(request.durationMonths()) 
                : null;
                
        routine.activate(startDate, endDate);

        Routine savedRoutine = routineRepository.save(routine);

        cancelPendingRoutineRequest(memberId);

        return routineDTOMapper.toResponse(savedRoutine, "Rutina personal creada con éxito.");
    }

    private void validateExercises(CreatePersonalRoutineRequest request, Long gymId) {
        request.days().forEach(day -> {
            day.exercises().forEach(exReq -> {
                Exercise exercise = exerciseRepository.findById(exReq.exerciseId())
                        .orElseThrow(() -> new ExerciseNotFoundException(exReq.exerciseId()));

                if (!exercise.getIsBase() && !exercise.getGymId().equals(gymId)) {
                    throw new UnauthorizedExerciseAccessException();
                }
            });
        });
    }

    private void cancelPendingRoutineRequest(Long memberId) {
        routineRequestRepository.findFirstByMemberIdAndStatus(memberId,
                        RoutineRequestStatus.PENDING)
                .ifPresent(pendingRequest -> {
                    pendingRequest.cancel();
                    routineRequestRepository.save(pendingRequest);
                });
    }
}