package com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.exercise;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exercise_muscle_group")
@Getter
@Setter
@NoArgsConstructor
public class ExerciseMuscleGroupJpaEntity {

    @EmbeddedId
    private ExerciseMuscleGroupKey id = new ExerciseMuscleGroupKey();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("exerciseId")
    @JoinColumn(name = "exercise_id")
    private ExerciseJpaEntity exercise;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("muscleGroupId")
    @JoinColumn(name = "muscle_group_id")
    private MuscleGroupJpaEntity muscleGroup;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary;
}
