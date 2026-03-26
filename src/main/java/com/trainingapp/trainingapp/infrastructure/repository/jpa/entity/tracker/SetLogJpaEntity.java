package com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.tracker;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "set_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SetLogJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "set_log_id")
    private Long id;

    @Column(name = "exercise_id", nullable = false)
    private Long exerciseId;

    @Column(name = "set_number", nullable = false)
    private Integer setNumber;

    @Column(name = "reps_performed", nullable = false)
    private Integer repsPerformed;

    @Column(name = "weight_lifted", nullable = false, precision = 6, scale = 2)
    private BigDecimal weightLifted;

    @Column(length = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private TrainingSessionJpaEntity session;
}