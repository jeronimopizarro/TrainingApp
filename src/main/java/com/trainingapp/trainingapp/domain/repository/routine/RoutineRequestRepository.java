package com.trainingapp.trainingapp.domain.repository.routine;

import com.trainingapp.trainingapp.domain.entity.routine.RoutineRequest;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineRequestStatus;

import java.util.List;
import java.util.Optional;

public interface RoutineRequestRepository {
    RoutineRequest save(RoutineRequest routineRequest);

    Optional<RoutineRequest> findById(Long id);

    // Para evitar que un alumno mande SPAM de solicitudes
    boolean existsByMemberIdAndStatus(Long memberId, RoutineRequestStatus status);

    // Dashboard del Entrenador (Traer todas las solicitudes PENDIENTES de su gimnasio)
    List<RoutineRequest> findByGymIdAndStatus(Long gymId, RoutineRequestStatus status);

    Optional<RoutineRequest> findFirstByMemberIdAndStatus(Long memberId,
                                                          RoutineRequestStatus status);

    Optional<RoutineRequest> findFirstByMemberIdAndStatusAndAssignedTrainerId(Long memberId,
                                                                              RoutineRequestStatus status,
                                                                              Long assignedTrainerId);
}
