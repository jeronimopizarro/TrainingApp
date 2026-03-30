package com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.exercise;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.BaseJpaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "muscle_group")
@Getter
@Setter
@NoArgsConstructor
public class MuscleGroupJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "muscle_group_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 255)
    private String description;

    // Relación bidireccional hacia la tabla intermedia
    @OneToMany(mappedBy = "muscleGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExerciseMuscleGroupJpaEntity> exercises = new ArrayList<>();
}
