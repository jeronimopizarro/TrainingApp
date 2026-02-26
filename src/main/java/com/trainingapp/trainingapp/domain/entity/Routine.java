package com.trainingapp.trainingapp.domain.entity;

import com.trainingapp.trainingapp.domain.Enum.RoutineStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class Routine {

    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    private Long memberId;
    private Long trainerId;
    private Long createdByUserId;

    private RoutineStatus status;

    private List<TrainingDay> days;

    public Routine(String name, Long memberId, Long trainerId,  Long createdByUserId) {
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("Name cannot be null.");
        }
        if (memberId == null) {
            throw new IllegalArgumentException("The routine must have an assigned member");
        }

        this.name = name;
        this.memberId = memberId;
        this.trainerId = trainerId;
        this.createdByUserId = createdByUserId;
        this.status = RoutineStatus.DRAFT;
        this.days = new ArrayList<>();
    }

    public TrainingDay addDay(String name){
        if (name == null ||  name.isBlank()){
            throw new IllegalArgumentException("Name cannot be null.");
        }

        int order = this.days.size() + 1;
        TrainingDay day = new TrainingDay(name, order);
        this.days.add(day);

        return day;
    }

    public void activate(Long requestingUserId, LocalDate startDate, LocalDate endDate){
        if (!canBeManagedBy(requestingUserId)){
            throw new IllegalArgumentException("The requesting user is not allowed to activate this routine.");
        }
        if (this.status != RoutineStatus.DRAFT){
            throw new IllegalStateException("Routine must be in DRAFT state to be activated.");
        }
        if (endDate != null && endDate.isBefore(startDate)){
            throw new IllegalArgumentException("End date cannot be before start date.");
        }

        this.status = RoutineStatus.ACTIVE;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    private boolean canBeManagedBy(Long userId) {
        // 1. El Creador siempre tiene permisos absolutos.
        // Si la creó el Socio, el Socio puede. Si la creó el Profe, el Profe puede.
        if (this.createdByUserId != null && this.createdByUserId.equals(userId)) {
            return true;
        }

        // 2. Si la rutina tiene un Profe asignado (quizás la creó un Admin o el coordinador),
        // el profe asignado también tiene derecho a activarla.
        if (this.trainerId != null && this.trainerId.equals(userId)) {
            return true;
        }

        return false;
    }
}
