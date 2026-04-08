package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.routine;

import com.trainingapp.trainingapp.domain.enums.routine.ExperienceLevel;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineRequestStatus;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.routine.RoutineRequestJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class RoutineRequestJpaRepositoryTest {

    @Autowired
    private RoutineRequestJpaRepository repository;

    @Test
    @DisplayName("Debería retornar true si el alumno tiene una solicitud PENDING")
    void existsByMemberIdAndStatus_ShouldReturnTrue_WhenPendingExists() {
        RoutineRequestJpaEntity entity = new RoutineRequestJpaEntity();
        entity.setMemberId(100L);
        entity.setGymId(10L);
        entity.setStatus(RoutineRequestStatus.PENDING);
        entity.setAvailableDays(3);
        entity.setExperienceLevel(ExperienceLevel.BEGINNER);
        entity.setPrimaryGoal("Hipertrofia");
        entity.setRequestDate(LocalDateTime.now());
        repository.save(entity);

        boolean exists = repository.existsByMemberIdAndStatus(100L, RoutineRequestStatus.PENDING);

        assertTrue(exists, "El repositorio debería detectar que existe una solicitud pendiente");
    }
}
