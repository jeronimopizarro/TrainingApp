package com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.exercise;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.BaseJpaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exercise")
@Getter
@Setter
@NoArgsConstructor
public class ExerciseJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "video_url", length = 255)
    private String videoUrl;

    @Column(name = "is_base")
    private Boolean isBase;

    @Column(name = "gym_id")
    private Long gymId; // Nullable por defecto en JPA (lo cual es correcto para is_base = true)

    @Column(name = "created_by_user_id")
    private Long createdByUserId;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExerciseMuscleGroupJpaEntity> muscleGroups = new ArrayList<>();

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
