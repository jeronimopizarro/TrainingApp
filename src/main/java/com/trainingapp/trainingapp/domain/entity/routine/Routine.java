package com.trainingapp.trainingapp.domain.entity.routine;

import com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus;
import com.trainingapp.trainingapp.web.dto.routine.UpdateRoutineRequest;
import com.trainingapp.trainingapp.web.dto.routine.UpdateRoutineRequest.UpdateTrainingDayRequest;
import java.util.Objects;
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
    private Long gymId;
    private RoutineStatus status;
    private boolean active;
    private List<TrainingDay> days;

    public Routine(String name, Long memberId, Long trainerId, Long createdByUserId, Long gymId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null.");
        }
        if (memberId == null) {
            throw new IllegalArgumentException("The routine must have an assigned member");
        }

        this.name = name;
        this.memberId = memberId;
        this.trainerId = trainerId;
        this.createdByUserId = createdByUserId;
        this.gymId = gymId;
        this.status = RoutineStatus.DRAFT;
        this.days = new ArrayList<>();
    }

    public TrainingDay addDay(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null.");
        }

        int order = this.days.size() + 1;
        TrainingDay day = new TrainingDay(name, order);
        this.days.add(day);

        return day;
    }

    public void activate(LocalDate startDate, LocalDate endDate) {
        if (this.status != RoutineStatus.DRAFT) {
            throw new IllegalStateException("Routine must be in DRAFT state to be activated.");
        }
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }

        this.status = RoutineStatus.ACTIVE;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = true;
    }

    public Routine duplicate(String newName, Long targetMemberId, Long targetTrainerId,
                             Long newCreatedByUserId) {
        Routine clonedRoutine = new Routine(newName, targetMemberId, targetTrainerId,
                newCreatedByUserId, this.gymId);

        copyDaysAndDetailsTo(clonedRoutine);

        return clonedRoutine;
    }

    private void copyDaysAndDetailsTo(Routine clonedRoutine) {
        for (TrainingDay sourceDay : this.days) {

            TrainingDay clonedDay = clonedRoutine.addDay(sourceDay.getName());

            copyDetailsToDay(sourceDay, clonedDay);
        }
    }

    private static void copyDetailsToDay(TrainingDay sourceDay, TrainingDay clonedDay) {
        for (RoutineDetail sourceDetail : sourceDay.getDetails()) {
            clonedDay.addDetails(sourceDetail.getExerciseId(), sourceDetail.getSets(),
                    sourceDetail.getRepsMin(), sourceDetail.getRepsMax(),
                    sourceDetail.getTargetRIR(), sourceDetail.getSuggestedWeight(),
                    sourceDetail.getNotes());
        }
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
        if (!this.active) {
            throw new IllegalStateException("The routine is already deleted from the system.");
        }
        this.active = false;
    }

    public void inactive() {
        if (this.status == RoutineStatus.INACTIVE) {
            throw new IllegalStateException("The routine is already archived.");
        }

        if (this.status == RoutineStatus.ACTIVE) {
            this.endDate = LocalDate.now();
        }

        this.status = RoutineStatus.INACTIVE;
    }

    public void validateForDeletion() {
        if (this.status != RoutineStatus.DRAFT) {
            throw new IllegalStateException("Only routines in DRAFT state can be permanently deleted. If it has history, please archive it (inactive) instead.");
        }
    }

    public void complete(){
        if (this.status != RoutineStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE routines can be marked as COMPLETED.");
        }

        this.status = RoutineStatus.COMPLETED;
        this.endDate = LocalDate.now();
    }
}