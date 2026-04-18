package com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.routine;

import com.trainingapp.trainingapp.domain.enums.routine.ExperienceLevel;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineRequestStatus;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.BaseJpaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "routine_requests")
@Getter
@Setter
@NoArgsConstructor
public class RoutineRequestJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long gymId;

    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RoutineRequestStatus status;

    @Column(name = "assigned_trainer_id")
    private Long assignedTrainerId;

    @Column(name = "routine_id")
    private Long routineId;

    @Column(name = "target_trainer_id")
    private Long targetTrainerId;

    @Column(name = "available_days", nullable = false)
    private Integer availableDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", nullable = false, length = 30)
    private ExperienceLevel experienceLevel;

    @Column(name = "injuries", length = 500)
    private String injuries;

    @Column(name = "primary_goal", nullable = false)
    private String primaryGoal;
}