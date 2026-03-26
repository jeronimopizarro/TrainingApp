package com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.tracker;

import com.trainingapp.trainingapp.domain.enums.tracker.SessionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "training_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingSessionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "routine_id")
    private Long routineId; // Nullable para entrenamientos libres

    @Column(name = "gym_id", nullable = false)
    private Long gymId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionStatus status;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SetLogJpaEntity> sets = new ArrayList<>();

    public void addSetLog(SetLogJpaEntity setLog) {
        sets.add(setLog);
        setLog.setSession(this);
    }
}