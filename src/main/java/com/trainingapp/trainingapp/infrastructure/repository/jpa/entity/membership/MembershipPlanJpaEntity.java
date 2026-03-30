package com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.membership;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.BaseJpaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "membership_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MembershipPlanJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_months", nullable = false)
    private int durationMonths;

    @Column(name = "gym_id", nullable = false)
    private Long gymId;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
