package com.trainingapp.trainingapp.infrastructure.repository.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "routine_detail")
@Getter
@Setter
@NoArgsConstructor
public class RoutineDetailJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routine_detail_id")
    private Long id;

    @Column(name = "order_number")
    private int orderNumber;

    private int sets;

    @Column(name = "reps_min")
    private int repsMin;

    @Column(name = "reps_max")
    private int repsMax;

    @Column(name = "suggested_weight")
    private Double suggestedWeight;

    @Column(name = "target_rir")
    private int targetRIR;

    private String notes;

    @Column(name = "exercise_id", nullable = false)
    private Long exerciseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_day_id", nullable = false)
    private TrainingDayJpaEntity trainingDay;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}