package com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.routine;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.BaseJpaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "training_day")
@Getter
@Setter
@NoArgsConstructor
public class TrainingDayJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "training_day_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "order_number")
    private int orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id", nullable = false)
    private RoutineJpaEntity routine;

    @OneToMany(mappedBy = "trainingDay", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineDetailJpaEntity> details = new ArrayList<>();
}
