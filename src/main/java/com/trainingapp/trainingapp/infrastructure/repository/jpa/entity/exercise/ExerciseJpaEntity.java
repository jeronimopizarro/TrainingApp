package com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.exercise;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exercise")
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE exercise SET deleted_at = CURRENT_TIMESTAMP WHERE exercise_id=?")
@SQLRestriction("deleted_at IS NULL") // Asegura que los findAll() ignoren los borrados
public class ExerciseJpaEntity {

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

    // Como todavía no tenemos la entidad Trainer mapeada, guardamos el ID suelto por ahora
    @Column(name = "creator_trainer_id")
    private Long creatorTrainerId;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExerciseMuscleGroupJpaEntity> muscleGroups = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
