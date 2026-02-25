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

    private RoutineStatus status;

    private List<TrainingDay> days;

    public Routine(String name, Long memberId, Long trainerId) {
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("Name cannot be null.");
        }
        if (memberId == null || trainerId == null) {
            throw new IllegalArgumentException("The routine must have an assigned member and trainer.");
        }

        this.name = name;
        this.memberId = memberId;
        this.trainerId = trainerId;
        this.status = RoutineStatus.DRAFT;
        this.days = new ArrayList<>();
    }

    public void addDay(String name){
        if (name == null ||  name.isBlank()){
            throw new IllegalArgumentException("Name cannot be null.");
        }
        if (!this.days.isEmpty()){
            throw new IllegalStateException("Cannot add more than one day.");
        }

        int order = this.days.size() + 1;
        TrainingDay day = new TrainingDay(name, order);
        this.days.add(day);
    }

    public void activate(LocalDate startDate, LocalDate endDate){
        if (startDate == null || endDate == null){
            throw new IllegalArgumentException("Start date and end date cannot be null.");
        }
        if (startDate.isAfter(endDate)){
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
        if (this.days.isEmpty()){
            throw  new IllegalStateException("Cannot activate a routine when the days list is empty.");
        }
        if (this.status != RoutineStatus.DRAFT){
            throw new IllegalStateException("Only DRAFT routines can be activated.");
        }

        this.startDate = startDate;
        this.endDate = endDate;
        this.status = RoutineStatus.ACTIVE;
    }
}
