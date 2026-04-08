package com.trainingapp.trainingapp.domain.entity.routine;

import com.trainingapp.trainingapp.domain.enums.routine.ExperienceLevel;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineRequestStatus;
import com.trainingapp.trainingapp.domain.exception.routine.InvalidRoutineRequestStateException;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RoutineRequest {

    private final Long id;
    private final Long memberId;
    private final Long gymId;
    private final LocalDateTime requestDate;
    private RoutineRequestStatus status;
    private Long assignedTrainerId; // El profe que finalmente tomó el caso
    private Long routineId;

    private final Long targetTrainerId; // El profe que el alumno eligió
    private final Integer availableDays; // ¿Cuántos días entrenará el miembro?
    private final ExperienceLevel experienceLevel;
    private final String injuries;
    private final String primaryGoal;

    private RoutineRequest(Long id, Long memberId, Long gymId, LocalDateTime requestDate,
                           RoutineRequestStatus status, Long assignedTrainerId, Long routineId,
                           Long targetTrainerId, Integer availableDays, ExperienceLevel experienceLevel,
                           String injuries, String primaryGoal) {
        this.id = id;
        this.memberId = memberId;
        this.gymId = gymId;
        this.requestDate = requestDate;
        this.status = status;
        this.assignedTrainerId = assignedTrainerId;
        this.routineId = routineId;

        this.targetTrainerId = targetTrainerId;
        this.availableDays = availableDays;
        this.experienceLevel = experienceLevel;
        this.injuries = injuries;
        this.primaryGoal = primaryGoal;

        validate();
    }

    private void validate() {
        if (this.memberId == null) throw new IllegalArgumentException("Member ID es requerido");
        if (this.gymId == null) throw new IllegalArgumentException("Gym ID es requerido");
        if (this.availableDays == null || this.availableDays < 1 || this.availableDays > 7) {
            throw new IllegalArgumentException("Los días disponibles deben estar entre 1 y 7");
        }
        if (this.experienceLevel == null) throw new IllegalArgumentException("El nivel de experiencia es requerido");
        if (this.primaryGoal == null || this.primaryGoal.trim().isEmpty()) {
            throw new IllegalArgumentException("El objetivo principal es requerido");
        }
    }

    public static RoutineRequest createNew(Long memberId, Long gymId, Long targetTrainerId,
                                           Integer availableDays, ExperienceLevel experienceLevel,
                                           String injuries, String primaryGoal) {
        return new RoutineRequest(null, memberId, gymId, LocalDateTime.now(),
                RoutineRequestStatus.PENDING, null, null,
                targetTrainerId, availableDays, experienceLevel, injuries, primaryGoal);
    }

    public static RoutineRequest restore(Long id, Long memberId, Long gymId, LocalDateTime requestDate,
                                         RoutineRequestStatus status, Long assignedTrainerId, Long routineId,
                                         Long targetTrainerId, Integer availableDays, ExperienceLevel experienceLevel,
                                         String injuries, String primaryGoal) {
        return new RoutineRequest(id, memberId, gymId, requestDate, status, assignedTrainerId, routineId,
                targetTrainerId, availableDays, experienceLevel, injuries, primaryGoal);
    }

    public void assignTrainer(Long trainerId) {
        if (this.status != RoutineRequestStatus.PENDING) {
            throw new IllegalStateException("Solo se pueden asignar solicitudes PENDING");
        }
        this.assignedTrainerId = trainerId;
        this.status = RoutineRequestStatus.IN_PROGRESS;
    }

    public void completeRequest(Long routineId) {
        if (this.status != RoutineRequestStatus.IN_PROGRESS) {
            throw new InvalidRoutineRequestStateException("Solo se pueden completar solicitudes que están IN_PROGRESS");
        }
        this.status = RoutineRequestStatus.COMPLETED;
        this.routineId = routineId;
    }

    public void cancel() {
        if (this.status != RoutineRequestStatus.PENDING) {
            throw new InvalidRoutineRequestStateException("Solo se pueden cancelar solicitudes pendientes.");
        }
        this.status = RoutineRequestStatus.CANCELLED;
    }
}