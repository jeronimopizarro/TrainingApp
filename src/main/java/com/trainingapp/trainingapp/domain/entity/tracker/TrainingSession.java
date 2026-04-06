package com.trainingapp.trainingapp.domain.entity.tracker;

import com.trainingapp.trainingapp.domain.enums.tracker.SessionStatus;
import com.trainingapp.trainingapp.domain.exception.tracker.InvalidSessionStateException;
import com.trainingapp.trainingapp.domain.exception.tracker.SessionMemberRequiredException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class TrainingSession {

    private Long id;
    private Long memberId;
    private Long routineId; // Opcional: Puede estar haciendo un entrenamiento libre
    private Long trainingDayId;
    private Long gymId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SessionStatus status;
    private List<SetLog> sets;

    public TrainingSession(Long id, Long memberId, Long routineId, Long trainingDayId, Long gymId,
                           LocalDateTime startTime,
                           LocalDateTime endTime, SessionStatus status, List<SetLog> sets) {
        this.id = id;
        this.memberId = memberId;
        this.routineId = routineId;
        this.trainingDayId = trainingDayId;
        this.gymId = gymId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.sets = sets != null ? new ArrayList<>(sets) : new ArrayList<>();
        validate();
    }

    private void validate() {
        if (this.memberId == null) throw new SessionMemberRequiredException();
        if (this.gymId == null)
            throw new InvalidSessionStateException("La sesión debe estar asociada a un gimnasio.");
        if (this.startTime == null)
            throw new InvalidSessionStateException("La sesión debe tener una fecha de inicio.");
    }

    public static TrainingSession startNew(Long memberId, Long routineId, Long trainingDayId, Long gymId) {
        return new TrainingSession(null, memberId, routineId, trainingDayId, gymId, LocalDateTime.now(),
                null, SessionStatus.IN_PROGRESS, new ArrayList<>());
    }

    public static TrainingSession restore(Long id, Long memberId, Long routineId, Long trainingDayId, Long gymId,
                                          LocalDateTime startTime, LocalDateTime endTime,
                                          SessionStatus status, List<SetLog> sets) {
        return new TrainingSession(id, memberId, routineId, trainingDayId, gymId, startTime, endTime, status,
                sets);
    }

    public void recordSet(Long exerciseId, Integer repsPerformed, BigDecimal weightLifted, Integer rir, String notes) {
        if (this.status != SessionStatus.IN_PROGRESS) {
            throw new InvalidSessionStateException("No se pueden agregar series a una sesión que no está en progreso.");
        }

        int nextSetNumber = (int) this.sets.stream()
                .filter(set -> set.getExerciseId().equals(exerciseId))
                .count() + 1;

        this.sets.add(SetLog.createNew(exerciseId, nextSetNumber, repsPerformed, weightLifted, rir, notes));
    }

    public void finish() {
        if (this.status != SessionStatus.IN_PROGRESS) {
            throw new InvalidSessionStateException("Solo se puede finalizar una sesión que está en progreso.");
        }
        this.status = SessionStatus.COMPLETED;
        this.endTime = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status != SessionStatus.IN_PROGRESS) {
            throw new InvalidSessionStateException("Solo se puede cancelar una sesión que está en progreso.");
        }
        this.status = SessionStatus.CANCELLED;
        this.endTime = LocalDateTime.now();
    }
}