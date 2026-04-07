package com.trainingapp.trainingapp.domain.entity.tracker;

import com.trainingapp.trainingapp.domain.enums.tracker.SessionStatus;
import com.trainingapp.trainingapp.domain.exception.tracker.InvalidSessionStateException;
import com.trainingapp.trainingapp.domain.exception.tracker.SessionMemberRequiredException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class TrainingSessionTest {

    @Test
    @DisplayName("Debería crear una sesión nueva en estado IN_PROGRESS")
    void shouldStartNewSessionSuccessfully() {
        TrainingSession session = TrainingSession.startNew(1L, 100L, 5L, 10L);

        assertNotNull(session.getStartTime(), "La fecha de inicio no debería ser nula");
        assertEquals(SessionStatus.IN_PROGRESS, session.getStatus(), "La sesión debe nacer en progreso");
        assertNull(session.getEndTime(), "La fecha de fin debe ser nula al iniciar");
        assertEquals(10L, session.getGymId(), "El gymId debe asignarse correctamente");
        assertTrue(session.getSets().isEmpty(), "La lista de series debe nacer vacía");
    }

    @Test
    @DisplayName("Debería lanzar SessionMemberRequiredException si intentamos iniciar sesión sin alumno")
    void shouldThrowExceptionWhenMemberIdIsNull() {
        assertThrows(
                SessionMemberRequiredException.class,
                () -> TrainingSession.startNew(null, 100L, 5L, 10L),
                "Debería fallar con tu excepción de dominio específica si el alumno es nulo"
        );
    }

    @Test
    @DisplayName("Debería lanzar error si se intenta registrar una serie en una sesión terminada")
    void shouldThrowExceptionWhenAddingSetToCompletedSession() {
        TrainingSession session = TrainingSession.startNew(1L, 100L, 5L, 10L);
        session.finish();

        assertThrows(
                InvalidSessionStateException.class,
                () -> session.recordSet(20L, 5, BigDecimal.valueOf(100), 1, "Nota"),
                "No debería dejar agregar series a una sesión que no está IN_PROGRESS"
        );
    }

    @Test
    @DisplayName("Al finalizar la sesión, debería cambiar a COMPLETED y registrar la hora")
    void shouldCompleteSession() {
        TrainingSession session = TrainingSession.startNew(1L, 100L, 5L, 10L);

        session.finish();

        assertEquals(SessionStatus.COMPLETED, session.getStatus());
        assertNotNull(session.getEndTime(), "La hora de finalización debió guardarse");
    }

    @Test
    @DisplayName("Debería agregar series con recordSet y calcular el promedio e1RM")
    void shouldRecordSetsAndCalculateAverageE1RM() {
        TrainingSession session = TrainingSession.startNew(1L, 100L, 5L, 10L);
        Long sentadillaId = 20L;

        // Utilizamos TU método nativo, que internamente instancia el SetLog y le calcula el 'nextSetNumber'
        session.recordSet(sentadillaId, 5, BigDecimal.valueOf(100), 1, "Buena serie");
        session.recordSet(sentadillaId, 3, BigDecimal.valueOf(100), 2, "Algo cansado");

        // Verificamos que tu método sumó correctamente los objetos a la lista
        assertEquals(2, session.getSets().size(), "Deberían haberse creado 2 series");

        // Act: Calculamos el promedio
        BigDecimal averageE1RM = session.calculateAverageE1RMForExercise(sentadillaId);

        // Assert: Validamos que la matemática combinada funcione y sea mayor a 0
        assertTrue(averageE1RM.compareTo(BigDecimal.ZERO) > 0, "El promedio debe ser mayor a 0");
    }
}
