package com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.exercise;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ExerciseMuscleGroupKey implements Serializable {
    //Esta clase crea una clave compuesta, para usarla como clave primaria en la tabla intermedia.
    @Column(name = "exercise_id")
    private Long exerciseId;

    @Column(name = "muscle_group_id")
    private Long muscleGroupId;
}
