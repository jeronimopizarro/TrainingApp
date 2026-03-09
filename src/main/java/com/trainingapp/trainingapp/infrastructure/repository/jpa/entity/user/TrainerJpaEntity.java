package com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trainers")
@Getter
@Setter
@NoArgsConstructor
public class TrainerJpaEntity extends UserJpaEntity {

    @Column(name = "gym_id", nullable = false)
    private Long gymId;

    @Column(length = 100)
    private String specialization;
}