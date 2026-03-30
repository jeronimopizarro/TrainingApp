package com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.access;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.BaseJpaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "access_logs")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccessLogJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "gym_id", nullable = false)
    private Long gymId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "access_granted", nullable = false)
    private boolean accessGranted;

    @Column(name = "message", nullable = false)
    private String message;
}
