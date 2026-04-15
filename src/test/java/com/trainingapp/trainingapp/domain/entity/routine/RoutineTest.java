package com.trainingapp.trainingapp.domain.entity.routine;

import com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus;
import com.trainingapp.trainingapp.domain.exception.routine.InvalidRoutineStateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RoutineTest {

    private Routine createRestoredRoutineWithTwoDays() {
        TrainingDay day1 = TrainingDay.restore(10L, "Día 1", 1, List.of());
        TrainingDay day2 = TrainingDay.restore(11L, "Día 2", 2, List.of());

        return Routine.restore(
                1L, "Rutina Test", null, null, 1L, 2L, 2L, 1L,
                RoutineStatus.ACTIVE, true, List.of(day1, day2)
        );
    }

    @Test
    @DisplayName("Debería retornar el primer día si el usuario nunca entrenó")
    void getNextTrainingDay_WhenNeverTrained_ReturnsFirstDay() {
        Routine routine = createRestoredRoutineWithTwoDays();

        TrainingDay nextDay = routine.getNextTrainingDay(null);

        assertEquals(10L, nextDay.getId());
    }

    @Test
    @DisplayName("Debería retornar el siguiente día cuando se completó el anterior")
    void getNextTrainingDay_WhenLastDayWasCompleted_ReturnsNextDay() {
        Routine routine = createRestoredRoutineWithTwoDays();

        TrainingDay nextDay = routine.getNextTrainingDay(10L);

        assertEquals(11L, nextDay.getId());
    }

    @Test
    @DisplayName("Debería reiniciar el ciclo y retornar el primer día al terminar la semana")
    void getNextTrainingDay_WhenEndOfCycle_ReturnsFirstDay() {
        Routine routine = createRestoredRoutineWithTwoDays();

        TrainingDay nextDay = routine.getNextTrainingDay(11L);

        assertEquals(10L, nextDay.getId());
    }

    @Test
    @DisplayName("Debería retornar el primer día si el ID enviado no pertenece a la rutina")
    void getNextTrainingDay_WhenUnknownDayId_ReturnsFirstDay() {
        Routine routine = createRestoredRoutineWithTwoDays();

        // Enviamos un ID inventado (99L) simulando un dato corrupto o viejo
        TrainingDay nextDay = routine.getNextTrainingDay(99L);

        assertEquals(10L, nextDay.getId());
    }

    @Test
    @DisplayName("Debería permitir validación para borrado en estado DRAFT o ACTIVE")
    void validateForDeletion_ShouldAllowDraftAndActive() {
        Routine draftRoutine = Routine.createNew("Draft", 1L, 2L, 2L, 1L);
        draftRoutine.validateForDeletion(); // No debe lanzar excepción

        Routine activeRoutine = createRestoredRoutineWithTwoDays();
        activeRoutine.validateForDeletion(); // No debe lanzar excepción
    }

    @Test
    @DisplayName("Debería lanzar excepción si se intenta borrar una rutina COMPLETED o INACTIVE")
    void validateForDeletion_ShouldThrowIfCompletedOrInactive() {
        Routine completedRoutine = Routine.restore(1L, "Test", null, null, 1L, 2L, 2L, 1L,
                RoutineStatus.COMPLETED, true, List.of());
        org.junit.jupiter.api.Assertions.assertThrows(InvalidRoutineStateException.class, completedRoutine::validateForDeletion);

        Routine inactiveRoutine = Routine.restore(2L, "Test", null, null, 1L, 2L, 2L, 1L,
                RoutineStatus.INACTIVE, true, List.of());
        org.junit.jupiter.api.Assertions.assertThrows(InvalidRoutineStateException.class, inactiveRoutine::validateForDeletion);
    }
}
