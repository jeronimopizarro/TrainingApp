package com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
public class AdminJpaEntity extends UserJpaEntity {

    @Column(name = "gym_id")
    private Long gymId;
}