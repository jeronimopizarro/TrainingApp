package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.tracker;

import com.trainingapp.trainingapp.domain.enums.tracker.SessionStatus;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.tracker.SetLogJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.tracker.TrainingSessionJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TrainingSessionJpaRepositoryTest {

    @Autowired
    private TrainingSessionJpaRepository repository;

    @Test
    @DisplayName("Debería encontrar una sesión activa (IN_PROGRESS) por ID de miembro")
    void shouldFindActiveSessionByMemberId() {

        // Creamos una sesión ACTIVA
        TrainingSessionJpaEntity activeSession = new TrainingSessionJpaEntity();
        activeSession.setMemberId(1L);
        activeSession.setGymId(10L);
        activeSession.setStartTime(LocalDateTime.now());
        activeSession.setStatus(SessionStatus.IN_PROGRESS);
        repository.save(activeSession);

        // Creamos una sesión TERMINADA
        TrainingSessionJpaEntity completedSession = new TrainingSessionJpaEntity();
        completedSession.setMemberId(1L);
        completedSession.setGymId(10L);
        completedSession.setStartTime(LocalDateTime.now().minusDays(1));
        completedSession.setEndTime(LocalDateTime.now());
        completedSession.setStatus(SessionStatus.COMPLETED);
        repository.save(completedSession);

        Optional<TrainingSessionJpaEntity> result =
                repository.findByMemberIdAndStatus(1L, SessionStatus.IN_PROGRESS);

        assertTrue(result.isPresent(), "Debería encontrar la sesión activa");
        assertEquals(SessionStatus.IN_PROGRESS, result.get().getStatus(), "El estado debe ser IN_PROGRESS");
    }

    @Test
    @DisplayName("Debería encontrar sesiones zombie que superen el límite de tiempo")
    void shouldFindZombieSessions() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold =
                now.minusHours(12); // Consideramos zombie a lo que tenga más de 12 horas

        // Sesión Zombie
        TrainingSessionJpaEntity zombieSession = new TrainingSessionJpaEntity();
        zombieSession.setMemberId(1L);
        zombieSession.setGymId(10L);
        zombieSession.setStartTime(now.minusHours(15));
        zombieSession.setStatus(SessionStatus.IN_PROGRESS);
        repository.save(zombieSession);

        // Sesión Normal
        TrainingSessionJpaEntity normalSession = new TrainingSessionJpaEntity();
        normalSession.setMemberId(2L);
        normalSession.setGymId(10L);
        normalSession.setStartTime(now.minusHours(2));
        normalSession.setStatus(SessionStatus.IN_PROGRESS);
        repository.save(normalSession);

        List<TrainingSessionJpaEntity> zombies = repository.findByStatusAndStartTimeBefore(SessionStatus.IN_PROGRESS, threshold);

        assertEquals(1, zombies.size(), "Debería encontrar exactamente 1 sesión zombie");
        assertEquals(1L, zombies.get(0).getMemberId(),
                "El zombie debería ser la sesión del socio 1");
    }

    @Test
    @DisplayName("Debería ejecutar la @Query manual para buscar el historial de un ejercicio")
    void shouldFindSessionsByMemberAndExercise() {
        LocalDateTime now = LocalDateTime.now();
        Long memberId = 1L;
        Long targetExerciseId = 100L;
        Long otherExerciseId = 200L;

        // Sesión VÁLIDA (Status Completed, entra en fecha y tiene el ejercicio 100)
        TrainingSessionJpaEntity session1 = new TrainingSessionJpaEntity();
        session1.setMemberId(memberId);
        session1.setGymId(10L);
        session1.setStartTime(now.minusMonths(2));
        session1.setStatus(SessionStatus.COMPLETED);

        SetLogJpaEntity set1 = new SetLogJpaEntity();
        set1.setExerciseId(targetExerciseId); // El que buscamos
        set1.setWeightLifted(BigDecimal.valueOf(50));
        set1.setSetNumber(1);
        set1.setRepsPerformed(10);
        set1.setRir(2);

        session1.addSetLog(set1);
        repository.save(session1);

        // Sesión INVÁLIDA (Status Completed, pero NO tiene el ejercicio buscado)
        TrainingSessionJpaEntity session2 = new TrainingSessionJpaEntity();
        session2.setMemberId(memberId);
        session2.setGymId(10L);
        session2.setStartTime(now.minusMonths(1));
        session2.setStatus(SessionStatus.COMPLETED);

        SetLogJpaEntity set2 = new SetLogJpaEntity();
        set2.setExerciseId(otherExerciseId); // Un ejercicio irrelevante
        set2.setWeightLifted(BigDecimal.valueOf(60));
        set2.setSetNumber(1);
        set2.setRepsPerformed(8);
        set2.setRir(1);

        session2.addSetLog(set2);
        repository.save(session2);

        // Act: Llamamos a tu JPQL escrito a mano
        List<TrainingSessionJpaEntity> result = repository.findSessionsByMemberAndExercise(
                memberId, targetExerciseId, now.minusMonths(6)
        );

        // Assert
        assertEquals(1, result.size(), "Tu @Query debe filtrar el JOIN correctamente y devolver solo 1 sesión");
        assertEquals(session1.getStartTime(), result.get(0).getStartTime());
    }
}