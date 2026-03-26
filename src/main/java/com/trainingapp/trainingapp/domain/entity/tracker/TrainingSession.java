package com.trainingapp.trainingapp.domain.entity.tracker;

import com.trainingapp.trainingapp.domain.enums.tracker.SessionStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class TrainingSession {

    private Long id;
    private Long memberId;
    private Long routineId; // Opcional: Puede estar haciendo un entrenamiento libre
    private Long gymId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SessionStatus status;
    private List<SetLog> sets;

    public TrainingSession(Long id, Long memberId, Long routineId, Long gymId,
                           LocalDateTime startTime,
                           LocalDateTime endTime, SessionStatus status, List<SetLog> sets) {
        this.id = id;
        this.memberId = memberId;
        this.routineId = routineId;
        this.gymId = gymId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.sets = sets != null ? new ArrayList<>(sets) : new ArrayList<>();
    }

    public static TrainingSession startNew(Long memberId, Long routineId, Long gymId) {
        if (memberId == null || gymId == null) {
            throw new IllegalArgumentException(
                    "El socio y el gimnasio son obligatorios para iniciar sesión.");
        }
        return new TrainingSession(null, memberId, routineId, gymId, LocalDateTime.now(), null,
                SessionStatus.IN_PROGRESS, new ArrayList<>());
    }

    public void finishSession() {
        if (this.status != SessionStatus.IN_PROGRESS) {
            throw new IllegalStateException("Solo se pueden finalizar sesiones en progreso.");
        }
        this.endTime = LocalDateTime.now();
        this.status = SessionStatus.COMPLETED;
    }

    public void addSet(SetLog setLog) {
        if (this.status != SessionStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "No se pueden agregar series a una sesión que no está en progreso.");
        }
        this.sets.add(setLog);
    }
}