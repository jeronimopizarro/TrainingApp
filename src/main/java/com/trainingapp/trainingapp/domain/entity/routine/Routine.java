package com.trainingapp.trainingapp.domain.entity.routine;

import com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus;
import com.trainingapp.trainingapp.domain.exception.routine.InvalidRoutineDatesException;
import com.trainingapp.trainingapp.domain.exception.routine.InvalidRoutineStateException;

import java.util.Objects;

import com.trainingapp.trainingapp.domain.exception.routine.RoutineMemberRequiredException;
import com.trainingapp.trainingapp.domain.exception.routine.RoutineNameRequiredException;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Routine {

    private final Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long memberId;
    private Long trainerId;
    private Long createdByUserId;
    private Long gymId;
    private RoutineStatus status;
    private boolean active;
    private List<TrainingDay> days;

    private Routine(Long id, String name, LocalDate startDate, LocalDate endDate, Long memberId,
                    Long trainerId, Long createdByUserId, Long gymId, RoutineStatus status,
                    boolean active, List<TrainingDay> days) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.memberId = memberId;
        this.trainerId = trainerId;
        this.createdByUserId = createdByUserId;
        this.gymId = gymId;
        this.status = status;
        this.active = active;
        this.days = days != null ? new ArrayList<>(days) : new ArrayList<>();
        validate();
    }

    private void validate() {
        if (this.name == null || this.name.isBlank()) throw new RoutineNameRequiredException();
        if (this.memberId == null) throw new RoutineMemberRequiredException();
    }

    public static Routine createNew(String name, Long memberId, Long trainerId,
                                    Long createdByUserId, Long gymId) {
        return new Routine(null, name, null, null, memberId, trainerId, createdByUserId, gymId,
                RoutineStatus.DRAFT, true, new ArrayList<>());
    }

    public static Routine restore(Long id, String name, LocalDate startDate, LocalDate endDate,
                                  Long memberId,
                                  Long trainerId, Long createdByUserId, Long gymId,
                                  RoutineStatus status,
                                  boolean active, List<TrainingDay> days) {
        return new Routine(id, name, startDate, endDate, memberId, trainerId, createdByUserId,
                gymId, status, active, days);
    }

    public TrainingDay addDay(String name) {
        int order = this.days.size() + 1;
        TrainingDay day = TrainingDay.createNew(name, order);
        this.days.add(day);
        return day;
    }

    public void activate(LocalDate startDate, LocalDate endDate) {
        if (this.status != RoutineStatus.DRAFT) throw new InvalidRoutineStateException();
        if (endDate != null && endDate.isBefore(startDate))
            throw new InvalidRoutineDatesException();

        this.status = RoutineStatus.ACTIVE;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = true;
    }

    public void update(String newName, Long newTrainerId, List<TrainingDay> incomingDays) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null.");
        }
        this.name = newName;
        this.trainerId = newTrainerId;

        syncDays(incomingDays);
    }

    private void syncDays(List<TrainingDay> incomingDays) {
        List<Long> incomingIds = incomingDays.stream()
                .map(TrainingDay::getId)
                .filter(Objects::nonNull)
                .toList();

        this.days.removeIf(day -> day.getId() != null && !incomingIds.contains(day.getId()));

        for (TrainingDay incomingDay : incomingDays) {
            if (incomingDay.getId() == null) {
                TrainingDay newDay = this.addDay(incomingDay.getName());
                newDay.syncDetails(incomingDay.getDetails());
            } else {
                this.days.stream()
                        .filter(day -> incomingDay.getId().equals(day.getId()))
                        .findFirst()
                        .ifPresent(existingDay -> {
                            existingDay.updateName(incomingDay.getName());
                            existingDay.syncDetails(incomingDay.getDetails());
                        });
            }
        }
    }

    public void deactivate() {
        if (!this.active) throw new InvalidRoutineStateException();
        this.active = false;
    }

    public void inactive() {
        if (this.status == RoutineStatus.INACTIVE) throw new InvalidRoutineStateException();
        if (this.status == RoutineStatus.ACTIVE) {
            this.endDate = LocalDate.now();
        }
        this.status = RoutineStatus.INACTIVE;
    }

    public void validateForDeletion() {
        if (this.status != RoutineStatus.DRAFT) throw new InvalidRoutineStateException();
    }

    public void complete() {
        if (this.status != RoutineStatus.ACTIVE) throw new InvalidRoutineStateException();
        this.status = RoutineStatus.COMPLETED;
        this.endDate = LocalDate.now();
    }

    public Routine duplicate(String newName, Long targetMemberId, Long targetTrainerId,
                             Long newCreatedByUserId) {
        Routine clonedRoutine =
                Routine.createNew(newName, targetMemberId, targetTrainerId, newCreatedByUserId,
                        this.gymId);
        copyDaysAndDetailsTo(clonedRoutine);
        return clonedRoutine;
    }

    private void copyDaysAndDetailsTo(Routine clonedRoutine) {
        for (TrainingDay sourceDay : this.days) {
            TrainingDay clonedDay = clonedRoutine.addDay(sourceDay.getName());
            clonedDay.copyDetailsFrom(sourceDay);
        }
    }
}