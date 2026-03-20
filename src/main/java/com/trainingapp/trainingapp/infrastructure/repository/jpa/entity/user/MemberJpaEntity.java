package com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
public class MemberJpaEntity extends UserJpaEntity {

    @Column(name = "gym_id", nullable = false)
    private Long gymId;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "primary_goal", length = 255)
    private String primaryGoal;
}