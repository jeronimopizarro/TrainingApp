package com.trainingapp.trainingapp.domain.entity.routine;

import com.trainingapp.trainingapp.domain.enums.routine.ExperienceLevel;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineRequestStatus;
import com.trainingapp.trainingapp.domain.exception.routine.InvalidRoutineRequestStateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoutineRequestTest {

    private RoutineRequest createRestoredRequest(RoutineRequestStatus status) {
        return RoutineRequest.restore(
                1L, 1L, 10L, LocalDateTime.now(),
                status,
                (status == RoutineRequestStatus.IN_PROGRESS) ? 2L : null, // Entrenador
                null,
                null, 3, ExperienceLevel.BEGINNER, "Ninguna", "Hipertrofia"
        );
    }

    @Test
    @DisplayName("Debería pasar a IN_PROGRESS y asignar profe si estaba PENDING")
    void shouldAssignTrainerAndSetInProgressWhenStatusIsPending() {
        RoutineRequest request = createRestoredRequest(RoutineRequestStatus.PENDING);

        request.assignTrainer(2L);

        assertEquals(RoutineRequestStatus.IN_PROGRESS, request.getStatus());
        assertEquals(2L, request.getAssignedTrainerId());
    }

    @Test
    @DisplayName("Debería lanzar error si intenta asignarse y no estaba PENDING")
    void shouldThrowExceptionWhenAssigningTrainerAndStatusIsNotPending() {
        RoutineRequest request = createRestoredRequest(RoutineRequestStatus.IN_PROGRESS);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> request.assignTrainer(3L));
        assertEquals("Solo se pueden asignar solicitudes PENDING", exception.getMessage());
    }

    @Test
    @DisplayName("Debería pasar a COMPLETED y guardar rutina si estaba IN_PROGRESS")
    void shouldSetStatusToCompletedWhenStatusIsInProgress() {
        RoutineRequest request = createRestoredRequest(RoutineRequestStatus.IN_PROGRESS);

        request.completeRequest(10L);

        assertEquals(RoutineRequestStatus.COMPLETED, request.getStatus());
        assertEquals(10L, request.getRoutineId());
    }

    @Test
    @DisplayName("Debería lanzar error si intenta completarse y no estaba IN_PROGRESS")
    void shouldThrowExceptionWhenCompletingRequestAndStatusIsNotInProgress() {
        RoutineRequest request = createRestoredRequest(RoutineRequestStatus.PENDING);

        InvalidRoutineRequestStateException exception = assertThrows(InvalidRoutineRequestStateException.class,
                () -> request.completeRequest(10L));
        assertEquals("Solo se pueden completar solicitudes que están IN_PROGRESS", exception.getMessage());
    }
}
